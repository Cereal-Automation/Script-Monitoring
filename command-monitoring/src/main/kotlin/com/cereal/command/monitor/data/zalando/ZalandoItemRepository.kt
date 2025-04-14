package com.cereal.command.monitor.data.zalando

import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.common.webclient.defaultJSoupClient
import com.cereal.command.monitor.data.zalando.models.Availability
import com.cereal.command.monitor.data.zalando.models.ZalandoProduct
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.models.Variant
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.select.Elements
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ZalandoItemRepository(
    private val logRepository: LogRepository,
    private val category: ZalandoProductCategory,
    private val website: ZalandoWebsite,
    private val monitorType: ZalandoMonitorType,
    private val randomProxy: RandomProxy? = null,
    private val timeout: Duration = 20.seconds,
) : ItemRepository {
    private val json = defaultJson()

    override suspend fun getItems(nextPageToken: String?): Page {
        val baseUrl = website.url.toHttpUrl()
        val urlBuilder = baseUrl.newBuilder().addPathSegment(category.paths[website]!!)

        val page = nextPageToken?.toInt() ?: 0
        urlBuilder.addQueryParameter("p", page.toString())

        if (monitorType == ZalandoMonitorType.NewReleases) {
            urlBuilder.addQueryParameter("order", "activation_date")
        }

        val document = defaultJSoupClient(urlBuilder.build().toString(), timeout, randomProxy?.invoke()).get()
        val links: Elements = document.select("article.z5x6ht._0xLoFW.JT3_zV.mo6ZnF._78xIQ- > a")

        val items =
            links.mapNotNull { link ->
                val href = link.attr("href")
                if (href.contains("https")) {
                    getProduct(href)
                } else {
                    null
                }
            }

        // For now only fetch the first page because we only support ZalandoMonitorType.NewReleases for now which sorts the results on activation_date.
        return Page(null, items)
    }

    private suspend fun getProduct(url: String): Item {
        val document = defaultJSoupClient(url, timeout, randomProxy?.invoke()).get()
        val jsonData =
            document
                .select("script[type=application/ld+json]")
                .first()
                ?.data() ?: throw RuntimeException("No product data found.")

        val product =
            json
                .decodeFromString<ZalandoProduct>(
                    jsonData,
                )

        return Item(
            id = product.sku,
            url = url,
            name = product.name,
            description = product.description,
            imageUrl = product.image.firstOrNull(),
            variants =
                product.offers.map {
                    Variant(
                        it.sku,
                        "Size ${extractSize(it.sku) ?: "N/A"}",
                        listOf(
                            ItemProperty.Stock(it.availability == Availability.InStock, null, null),
                            ItemProperty.Price(
                                it.price,
                                Currency.fromCode(it.priceCurrency) ?: website.defaultCurrency,
                            ),
                        ),
                    )
                },
        )
    }

    private fun extractSize(sku: String): String? {
        val regex = """(\d{2})000${'$'}""".toRegex()
        val matchResult = regex.find(sku)
        return matchResult?.groupValues?.get(1)
    }
}
