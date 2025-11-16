package com.cereal.command.monitor.data.bolcom

import com.cereal.command.monitor.data.bolcom.httpclient.defaultBolComHttpClient
import com.cereal.command.monitor.data.bolcom.httpclient.exception.ProductUnavailableException
import com.cereal.command.monitor.data.bolcom.httpclient.exception.ProxyUnavailableException
import com.cereal.command.monitor.data.bolcom.httpclient.model.BolProduct
import com.cereal.command.monitor.data.bolcom.httpclient.model.BolcomSearchResponse
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class BolcomWebDataSource(
    private val logRepository: LogRepository,
    private val randomProxy: RandomProxy?,
) {
    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 18_7_2 like Mac OS X) " +
                "AppleWebKit/605.1.15 (KHTML, like Gecko) " +
                "Version/26.0 Mobile/15E148 Safari/604.1"
        private const val BOL_COM_BASE_URL = "https://www.bol.com"
    }

    private var client: HttpClient? = null
    private val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }
    }

    private suspend fun getHttpClient(): HttpClient =
        client ?: run {
            defaultBolComHttpClient(
                logRepository = logRepository,
                httpProxy = randomProxy?.invoke(),
                defaultHeaders = mapOf(HttpHeaders.UserAgent to USER_AGENT),
            ).also {
                client = it
            }
        }

    /**
     * Fetches a list of items by querying an external service using the provided EAN (European Article Number).
     *
     * The method performs an HTTP GET request to fetch product data associated with the given EAN. It parses
     * the response, processes the resulting data, and maps the processed data to a list of `Item` objects.
     *
     * @param ean The European Article Number used to fetch product data.
     * @return A list of `Item` objects corresponding to the products found for the given EAN. Returns an empty list if no products are found or in case of an error.
     */
    suspend fun fetchItemsByEan(ean: String): List<Item> {
        val response =
            performRequest(
                url = "$BOL_COM_BASE_URL/nl/nl/s.data?searchtext=$ean",
                method = HttpMethod.Get,
            ) {}

        val raw = response.bodyAsText()
        val asJson = runCatching { json.parseToJsonElement(raw) }.getOrNull()
        val decoded =
            if (asJson is JsonArray) {
                tryDecodeInternedTable(raw)
            } else {
                asJson
            }
        val normalized: JsonElement = decoded ?: asJson ?: JsonNull
        val products = collectProducts(normalized)
        if (products.isEmpty()) return emptyList()

        return products.mapNotNull { productData -> mapSearchResultToItem(productData) }
    }

    private fun collectProducts(el: JsonElement): List<BolProduct> {
        val response =
            runCatching {
                json.decodeFromJsonElement<BolcomSearchResponse>(el)
            }.getOrNull() ?: return emptyList()

        val template =
            response.customer
                ?.basket
                ?.totalQuantity
                ?.routesSearchPage
                ?.data
                ?.template
                ?: return emptyList()

        val products = mutableListOf<BolProduct>()

        for (region in template.regions) {
            for (slot in region.slots) {
                if (slot.type == "ProductItem") {
                    val product = slot.props?.product ?: continue
                    val productId = product.id ?: continue

                    val brand =
                        product.relatedParties
                            .firstOrNull { it.role == "BRAND" }
                            ?.party?.name

                    val imageUrl =
                        product.assets
                            .firstOrNull()
                            ?.renditions?.firstOrNull()
                            ?.url

                    val description =
                        product.attributes
                            .firstOrNull { it.name == "Description" }
                            ?.values?.firstOrNull()
                            ?.value

                    val bestOffer = product.bestSellingOffer
                    val price = bestOffer?.sellingPrice?.price?.amount
                    val discount = bestOffer?.sellingPriceDiscountOnStrikethroughPrice?.amount?.amount
                    val regularPrice = bestOffer?.strikethroughPrice?.price?.amount
                    val sellerName = bestOffer?.retailer?.name ?: bestOffer?.retailer?.id

                    val deliveryDesc = bestOffer?.bestDeliveryOption?.deliveryDescription
                    val orderable =
                        (
                            deliveryDesc != null &&
                                (
                                    deliveryDesc.contains("Op voorraad", ignoreCase = true) ||
                                        deliveryDesc.contains("Voor 23:00", ignoreCase = true) ||
                                        deliveryDesc.contains("morgen in huis", ignoreCase = true)
                                )
                        ) ||
                            bestOffer?.deliveredWithin48Hours == true

                    products.add(
                        BolProduct(
                            productId = productId,
                            title = product.title,
                            slug = product.url,
                            brand = brand,
                            price = price,
                            discount = discount,
                            regularPrice = regularPrice,
                            orderable = orderable,
                            imageUrl = imageUrl,
                            seller = sellerName,
                            description = description,
                        ),
                    )
                }
            }
        }

        return products
    }

    private fun mapSearchResultToItem(productData: BolProduct): Item? {
        val title = productData.title ?: return null
        val properties = mutableListOf<ItemProperty>()

        properties.add(
            ItemProperty.Stock(
                isInStock = productData.orderable,
                amount = null,
                level = if (productData.orderable) ":white_check_mark:" else ":x:",
            ),
        )

        productData.seller?.let { seller ->
            properties.add(
                ItemProperty.Custom(
                    name = "Seller",
                    value = seller,
                ),
            )
        }

        productData.brand?.let { brand ->
            if (brand.isNotBlank()) {
                properties.add(ItemProperty.Custom(name = "Brand", value = brand))
            }
        }

        productData.price?.let { price ->
            properties.add(
                ItemProperty.Custom(
                    name = "Price",
                    value =
                        buildString {
                            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
                            currencyFormatter.currency = java.util.Currency.getInstance(Currency.EUR.code)

                            append(currencyFormatter.format(BigDecimal.valueOf(price)))
                            productData.regularPrice?.let { regularPrice ->
                                append(" ")
                                append("~~${currencyFormatter.format(BigDecimal.valueOf(regularPrice))}~~")
                            }
                        },
                ),
            )
        }

        productData.discount?.let { price ->
            properties.add(
                ItemProperty.Custom(
                    name = "Discount",
                    value =
                        buildString {
                            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
                            currencyFormatter.currency = java.util.Currency.getInstance(Currency.EUR.code)
                            append(currencyFormatter.format(BigDecimal.valueOf(price)))
                        },
                ),
            )
        }

        return Item(
            id = productData.productId,
            url =
                buildString {
                    append(BOL_COM_BASE_URL)
                    append(productData.slug)
                },
            name = title,
            description = productData.description,
            imageUrl = productData.imageUrl,
            variants = emptyList(),
            properties = properties,
        )
    }

    private suspend fun performRequest(
        url: String,
        method: HttpMethod,
        block: HttpRequestBuilder.() -> Unit,
    ): HttpResponse {
        try {
            val response =
                getHttpClient().request {
                    this.url(url)
                    this.method = method
                    apply(block)
                }

            return response
        } catch (e: ResponseException) {
            val isProxyUnavailable =
                e is ServerResponseException && e.response.status == HttpStatusCode.ServiceUnavailable
            val isProxyForbidden =
                e is ClientRequestException && e.response.status == HttpStatusCode.Forbidden

            if (isProxyUnavailable || isProxyForbidden) {
                this@BolcomWebDataSource.client?.close()
                this@BolcomWebDataSource.client = null
                throw ProxyUnavailableException("Request received from server as untrusted")
            }

            val isUnauthorized =
                e is ClientRequestException && e.response.status == HttpStatusCode.Unauthorized

            if (isUnauthorized) {
                this@BolcomWebDataSource.client?.close()
                this@BolcomWebDataSource.client = null
                throw ProductUnavailableException("Product is currently unavailable")
            }

            throw e
        }
    }
}
