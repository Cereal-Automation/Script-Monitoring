package com.cereal.script.commands.monitor.data.nike

import com.cereal.script.commands.monitor.data.factories.HttpClientFactory
import com.cereal.script.commands.monitor.data.factories.JsonFactory
import com.cereal.script.commands.monitor.data.factories.WebClientFactory
import com.cereal.script.commands.monitor.data.nike.models.NikeResponse
import com.cereal.script.commands.monitor.data.nike.models.Product
import com.cereal.script.commands.monitor.data.nike.models.Wall
import com.cereal.script.commands.monitor.models.Currency
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.Page
import com.cereal.script.commands.monitor.repository.ItemRepository
import com.cereal.sdk.models.proxy.RandomProxy
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import one.ifelse.tools.useragent.RandomUserAgent
import org.htmlunit.html.HtmlPage
import org.htmlunit.html.HtmlScript
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Taken from https://www.trickster.dev/post/scraping-product-data-from-nike/
 *
 * 1. Fetch the product list page HTML with nested JSON. Use it to extract some initial product data and the first
 * value for endpoint parameter to start API scraping.
 * 2. Scrape product feed API by traversing all pages.
 */
class NikeItemRepository(
    private val category: ScrapeCategory,
    private val randomProxy: RandomProxy?,
    private val timeout: Duration = 20.seconds,
) : ItemRepository {
    private val json = JsonFactory.create()
    private val defaultCurrencyCode = Currency.USD
    private val defaultHeaders =
        mapOf(
            HttpHeaders.ContentType to ContentType.Application.Json,
            HttpHeaders.Accept to ContentType.Application.Json,
            HttpHeaders.AcceptEncoding to "gzip, deflate, br",
            HttpHeaders.AcceptLanguage to "en-GB,en;q=0.9",
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

    override suspend fun getItems(nextPageToken: String?): Page =
        nextPageToken?.let {
            createNextPageFlow(it)
        } ?: createFirstPageFlow(category.url)

    private suspend fun createFirstPageFlow(scrapeUrl: String): Page =
        createPage {
            val webClient = WebClientFactory.create(randomProxy?.invoke())

            try {
                val page: HtmlPage = webClient.getPage(scrapeUrl)
                val scriptElement = page.getElementById("__NEXT_DATA__") as? HtmlScript

                if (scriptElement != null) {
                    val jsonData = scriptElement.textContent
                    json
                        .decodeFromString<NikeResponse>(
                            jsonData,
                        ).props.pageProps.initialState.wall
                } else {
                    throw Exception("Script element with ID '__NEXT_DATA__' not found.")
                }
            } catch (e: Exception) {
                throw Exception("Error fetching data from $scrapeUrl: ${e.message}")
            } finally {
                webClient.close()
            }
        }

    private suspend fun createNextPageFlow(next: String): Page =
        createPage {
            val response =
                HttpClientFactory.create(timeout, randomProxy?.invoke(), defaultHeaders = defaultHeaders).get(next)
            response.body<Wall>()
        }

    private suspend fun createPage(extractWall: suspend () -> Wall): Page {
        val wall = extractWall()

        val items = mutableListOf<Item>()
        wall.productGroupings.forEach { productGrouping ->
            productGrouping.products?.forEach { product ->
                items.add(product.toItem())
            }
        }

        // The first page nikeResponse.wall.pageData.next is filled, all subsequent pages nikeResponse.wall.pages.next is filled.
        val nextPagePath =
            wall.pages.next
                .ifEmpty { wall.pageData.next }
        if (nextPagePath.isNotEmpty()) {
            return Page("https://api.nike.com/$nextPagePath", items)
        } else {
            return Page(null, items)
        }
    }

    private fun Product.toItem(): Item {
        val description = "Product code: ${this.productCode}\nColor: ${this.displayColors.colorDescription}"

        return Item(
            id = this.globalProductId,
            url = this.pdpUrl.url,
            name = this.copy.title,
            description = description,
            imageUrl = this.colorwayImages.squarishURL,
            properties =
                listOf(
                    ItemProperty.Price(
                        BigDecimal(this.prices.currentPrice),
                        Currency
                            .fromCode(this.prices.currency)
                            ?: defaultCurrencyCode,
                    ),
                ),
        )
    }
}
