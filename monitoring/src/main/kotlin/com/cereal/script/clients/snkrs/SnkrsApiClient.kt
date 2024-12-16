package com.cereal.script.clients.snkrs

import com.cereal.script.clients.snkrs.models.Object
import com.cereal.script.clients.snkrs.models.ProductInfo
import com.cereal.script.clients.snkrs.models.SnkrsResponse
import com.cereal.script.commands.monitor.domain.models.Currency
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.Variant
import com.cereal.sdk.models.proxy.RandomProxy
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import one.ifelse.tools.useragent.RandomUserAgent
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SnkrsApiClient(
    private val randomProxy: RandomProxy? = null,
    private val timeout: Duration = 20.seconds,
) {
    private val url = "https://api.nike.com/product_feed/threads/v3/"
    private val defaultCurrencyCode = Currency.USD
    private val client: HttpClient =
        HttpClient {
            engine {
                randomProxy?.let {
                    // TODO
                    proxy = ProxyBuilder.http("http://TODO:TODO")
                }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    },
                )
            }
            install(Logging) {
                level = LogLevel.BODY
            }
            install(HttpTimeout) {
                requestTimeoutMillis = timeout.inWholeMilliseconds
            }
            install(ContentEncoding) {
                gzip()
                deflate()
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = "TODO", password = "TODO")
                    }
                }
            }
            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
                header(HttpHeaders.AcceptLanguage, "en-GB,en;q=0.9")
                header("appid", "com.nike.commerce.snkrs.web")
                header("DNT", "1")
                header("nike-api-caller-id", "nike:snkrs:web:1.0")
                header(HttpHeaders.Origin, "https://www.nike.com")
                header(HttpHeaders.Referrer, "https://www.nike.com/")
                header("Sec-Fetch-Dest", "empty")
                header("Sec-Fetch-Mode", "cors")
                header("Sec-Fetch-Site", "same-site")
                header(
                    HttpHeaders.UserAgent,
                    RandomUserAgent.random({ it.deviceCategory == "mobile" && it.userAgent.contains("Chrome") }),
                )
                header(HttpHeaders.CacheControl, "no-cache, no-store, must-revalidate")
                header(HttpHeaders.Pragma, "no-cache")
                header(HttpHeaders.Expires, "0")
            }
        }

    suspend fun getProducts(
        locale: Locale,
        anchor: Int,
        count: Int,
    ): List<Item> {
        val response =
            client
                .get(url) {
                    parameter("anchor", anchor)
                    parameter("count", count)
                    parameter("filter", "marketplace(${locale.country})")
                    parameter("filter", "language(${locale.languageCode})")
                    parameter("filter", "channelId(010794e5-35fe-4e32-aaff-cd2c74f89d61)")
                    parameter("filter", "exclusiveAccess(true,false)")
                }

        val snkrsResponse = response.body<SnkrsResponse>()
        val allProducts = mutableListOf<Item>()

        snkrsResponse.objects.forEach { item ->
            item.productInfo
                ?.mapNotNull { product ->
                    if (product.availability.available && product.merchProduct.status == "ACTIVE") {
                        createItem(locale, item, product)
                    } else {
                        null
                    }
                }?.also {
                    allProducts.addAll(it)
                }
        }

        return allProducts
    }

    private fun createItem(
        locale: Locale,
        item: Object,
        product: ProductInfo,
    ): Item? {
        val sizes = mutableListOf<Variant>()

        product.availableGtins.forEach { availableGtin ->
            val gtin = availableGtin.gtin

            if (availableGtin.available) {
                product.skus.forEach { sku ->
                    if (sku.gtin == gtin) {
                        sizes.add(Variant(sku.nikeSize, availableGtin.level != "OOS", availableGtin.level))
                        return@forEach
                    }
                }
            }
        }

        return if (sizes.isNotEmpty()) {
            val id = product.productContent.globalPid
            val title = product.productContent.fullTitle
            val description = product.productContent.colorDescription
            val url = "https://www.nike.com/${locale.country}/launch/t/${product.productContent.slug}"
            val thumbnail =
                item.publishedContent.nodes
                    .firstOrNull()
                    ?.nodes
                    ?.firstOrNull()
                    ?.properties
                    ?.squarishURL
            val price = BigDecimal(product.merchPrice.currentPrice)
            val currency =
                Currency
                    .fromCode(product.merchPrice.currency)
                    ?: defaultCurrencyCode
            val styleCode = product.merchProduct.styleColor

            Item(
                id,
                url,
                title,
                description,
                thumbnail,
                properties =
                    listOf(
                        ItemProperty.Variants(sizes),
                        ItemProperty.Price(price, currency),
                        ItemProperty.Custom("Style code", styleCode),
                    ),
            )
        } else {
            null
        }
    }
}
