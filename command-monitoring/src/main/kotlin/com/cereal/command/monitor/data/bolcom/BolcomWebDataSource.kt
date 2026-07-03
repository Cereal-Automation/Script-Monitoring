package com.cereal.command.monitor.data.bolcom

import com.bol.mapping.BolProduct
import com.bol.mapping.parseBolProducts
import com.cereal.command.monitor.data.bolcom.httpclient.defaultBolComHttpClient
import com.cereal.command.monitor.data.bolcom.httpclient.exception.ProductUnavailableException
import com.cereal.command.monitor.data.bolcom.httpclient.exception.ProxyUnavailableException
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
        val products = parseBolProducts(normalized)

        return products.map { it.toItem() }
    }

    private fun BolProduct.toItem(): Item {
        val offer = bestSellingOffer
        val properties = buildList {
            price?.toBigDecimalOrNull()?.let { add(ItemProperty.Price(it, Currency.EUR)) }
            brandName?.let { add(ItemProperty.Custom("Brand", it)) }
            offer?.retailer?.name?.let { add(ItemProperty.Custom("Seller", it)) }
            discountPercentage?.let { pct ->
                val was = referencePrice
                add(ItemProperty.Custom("Discount", if (was != null) "$pct% (was €$was)" else "$pct%"))
            }
            offer?.bestDeliveryOption?.deliveryDescription?.let { add(ItemProperty.Custom("Shipping", it)) }
            add(stockProperty)
        }

        return Item(
            id = id,
            url = "$BOL_COM_BASE_URL$url",
            name = title,
            description = description,
            imageUrl = primaryImageUrl,
            properties = properties,
        )
    }

    /**
     * There's no explicit "in stock" flag; availability is inferred from whether a
     * selling offer exists at all, whether it's a pre-order (future release date), and
     * whether it's running low (`isScarce`). `bestDeliveryOption.deliveryDescription` is
     * display text about shipping time, not a reliable stock signal, so it isn't used here.
     */
    private val BolProduct.stockProperty: ItemProperty.Stock
        get() {
            val offer = bestSellingOffer
                ?: return ItemProperty.Stock(isInStock = false, amount = null, level = "Unavailable")
            if (offer.bestDeliveryOption?.productReleaseDate != null) {
                return ItemProperty.Stock(isInStock = false, amount = null, level = "Preorder")
            }
            return ItemProperty.Stock(isInStock = true, amount = null, level = if (offer.isScarce) "Low stock" else null)
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
