package com.cereal.script.commands.monitor.data.snkrs

import com.cereal.script.commands.monitor.data.factories.HttpClientFactory
import com.cereal.script.commands.monitor.data.snkrs.models.Object
import com.cereal.script.commands.monitor.data.snkrs.models.ProductInfo
import com.cereal.script.commands.monitor.data.snkrs.models.SnkrsResponse
import com.cereal.script.commands.monitor.models.Currency
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.Variant
import com.cereal.sdk.models.proxy.RandomProxy
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
    private val defaultHeaders =
        mapOf(
            HttpHeaders.ContentType to ContentType.Application.Json,
            HttpHeaders.Accept to ContentType.Application.Json,
            HttpHeaders.AcceptEncoding to "gzip, deflate, br",
            HttpHeaders.AcceptLanguage to "en-GB,en;q=0.9",
            "appid" to "com.nike.commerce.snkrs.web",
            "DNT" to "1",
            "nike-api-caller-id" to "nike:snkrs:web:1.0",
            HttpHeaders.Origin to "https://www.nike.com",
            HttpHeaders.Referrer to "https://www.nike.com/",
            "Sec-Fetch-Dest" to "empty",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Site" to "same-site",
            HttpHeaders.UserAgent to
                RandomUserAgent.random({ it.deviceCategory == "mobile" && it.userAgent.contains("Chrome") }),
            HttpHeaders.CacheControl to "no-cache, no-store, must-revalidate",
            HttpHeaders.Pragma to "no-cache",
            HttpHeaders.Expires to "0",
        )

    suspend fun getProducts(
        locale: Locale,
        anchor: Int,
        count: Int,
    ): List<Item> {
        val response =
            HttpClientFactory
                .create(timeout, randomProxy?.invoke(), defaultHeaders = defaultHeaders)
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