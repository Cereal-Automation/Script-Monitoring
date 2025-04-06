package com.cereal.command.monitor.data.zalando

import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.common.webclient.defaultJSoupClient
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
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ZalandoItemRepository(
    private val logRepository: LogRepository,
    private val category: ZalandoProductCategory,
    private val website: ZalandoWebsite,
    private val randomProxy: RandomProxy? = null,
    private val timeout: Duration = 20.seconds,
) : ItemRepository {
    private val json = defaultJson()

    override suspend fun getItems(nextPageToken: String?): Page {
        val baseUrl = website.url.toHttpUrl()
        val urlBuilder = baseUrl.newBuilder().addPathSegment(category.paths[website]!!)

        val page = nextPageToken?.toInt() ?: 0
        urlBuilder.addQueryParameter("p", page.toString())
        urlBuilder.addQueryParameter("order", "activation_date")

        // TODO: Detect last page or just scrape 1 page in case of new releases sorting.

        val document = defaultJSoupClient(urlBuilder.build().toString(), timeout, randomProxy?.invoke()).get()
        val baseUri = document.baseUri()
        print(baseUri)

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

        return Page((page + 1).toString(), items)
    }

    private fun getProduct(url: String): Item {
        val document = Jsoup.connect(url).get()
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
                            ItemProperty.Stock(it.availability == "http://schema.org/InStock", null, null),
                            ItemProperty.Price(
                                BigDecimal(it.price),
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
