package com.cereal.command.monitor.data.shopify

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.common.useragent.MOBILE_USER_AGENTS
import com.cereal.command.monitor.data.shopify.models.ShopifyResponse
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.models.Variant
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import java.net.URL
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val PRODUCTS_JSON_PATH = "products.json"

class ShopifyItemRepository(
    private val logRepository: LogRepository,
    private val website: ShopifyWebsite,
    private val randomProxy: RandomProxy? = null,
    private val timeout: Duration = 20.seconds,
) : ItemRepository {
    private val defaultHeaders =
        mapOf(
            HttpHeaders.UserAgent to MOBILE_USER_AGENTS.random(),
            HttpHeaders.CacheControl to "no-cache, no-store, must-revalidate",
            HttpHeaders.Pragma to "no-cache",
            HttpHeaders.Expires to "0",
        )
    private val json = defaultJson()

    override suspend fun getItems(nextPageToken: String?): Page {
        val url = website.url.append(PRODUCTS_JSON_PATH)

        val response =
            defaultHttpClient(timeout, randomProxy?.invoke(), logRepository, defaultHeaders = defaultHeaders).get(url) {
                parameter("limit", "250")
                nextPageToken?.let {
                    parameter("page", it)
                }
            }

        // Use this method of reading json instead of `response.body<ShopifyResponse>()` because when proguard is applied
        // that will raise a runtime error saying that the serializer couldn't be loaded. Most likely because proguard
        // strips the reified information.
        val bodyText = response.bodyAsText()
        val shopifyResponse = json.decodeFromString(ShopifyResponse.serializer(), bodyText)

        val items =
            shopifyResponse.products.map { product ->
                val productUrl = website.url.getBaseUrl()?.append("/products/${product.handle}")

                Item(
                    id = product.id,
                    url = productUrl,
                    name = product.title,
                    description = product.bodyHtml?.stripHtml(),
                    imageUrl = product.images.firstOrNull()?.src,
                    properties =
                        listOfNotNull(
                            product.publishedAt?.let { ItemProperty.PublishDate(it) },
                            ItemProperty.Variants(product.variants.map { Variant(it.title, it.available) }),
                        ),
                )
            }

        return if (items.isEmpty()) {
            Page(null, emptyList())
        } else {
            val nextPageNumber = ((nextPageToken?.toInt()) ?: 1) + 1
            Page(nextPageNumber.toString(), items)
        }
    }

    private fun String.append(path: String): String =
        if (this.endsWith("/") || path.startsWith("/")) {
            "$this$path"
        } else {
            "$this/$path"
        }

    private fun String.getBaseUrl(): String? =
        try {
            val url = URL(this)
            "${url.protocol}://${url.host}${if (url.port != -1) ":${url.port}" else ""}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    private fun String.stripHtml(): String = replace(Regex("<[^>]*>"), "").trim()
}
