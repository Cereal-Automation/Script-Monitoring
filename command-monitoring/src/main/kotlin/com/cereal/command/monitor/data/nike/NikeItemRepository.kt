package com.cereal.command.monitor.data.nike

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.common.useragent.MOBILE_USER_AGENTS
import com.cereal.command.monitor.data.common.webclient.defaultWebClient
import com.cereal.command.monitor.data.nike.models.NikeResponse
import com.cereal.command.monitor.data.nike.models.Product
import com.cereal.command.monitor.data.nike.models.Wall
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
    private val logRepository: LogRepository,
    private val category: ScrapeCategory,
    private val randomProxy: RandomProxy? = null,
    private val timeout: Duration = 20.seconds,
) : ItemRepository {
    private val json = defaultJson()
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
            HttpHeaders.UserAgent to MOBILE_USER_AGENTS.random(),
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
            val webClient = defaultWebClient(randomProxy?.invoke())

            try {
                val page: HtmlPage = webClient.getPage(scrapeUrl)
                val scriptElement = page.getElementById("__NEXT_DATA__") as? HtmlScript

                if (scriptElement != null) {
                    // FIXME: Rewrite to use Jsoup for more robust HTML parsing and better performance.
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
                defaultHttpClient(timeout, randomProxy?.invoke(), logRepository, defaultHeaders = defaultHeaders).get(next)

            // Use this method of reading json instead of `response.body<Wall>()` because when proguard is applied
            // that will raise a runtime error saying that the serializer couldn't be loaded. Most likely because proguard
            // strips the reified information.
            val bodyText = response.bodyAsText()
            json.decodeFromString(Wall.serializer(), bodyText)
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
