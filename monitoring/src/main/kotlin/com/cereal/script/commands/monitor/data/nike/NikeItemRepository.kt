package com.cereal.script.commands.monitor.data.nike

import com.cereal.script.commands.monitor.data.nike.models.NikeResponse
import com.cereal.script.commands.monitor.data.nike.models.Product
import com.cereal.script.commands.monitor.data.nike.models.Wall
import com.cereal.script.commands.monitor.domain.ItemRepository
import com.cereal.script.commands.monitor.domain.models.Currency
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.Page
import com.cereal.sdk.models.proxy.RandomProxy
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.ProxyBuilder
import it.skrape.fetcher.basic
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.script
import kotlinx.serialization.json.Json
import java.math.BigDecimal

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
) : ItemRepository {
    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    private val defaultCurrencyCode = Currency.USD

    override suspend fun getItems(nextPageToken: String?): Page =
        nextPageToken?.let {
            createNextPageFlow(it)
        } ?: createFirstPageFlow(category.url)

    private suspend fun createFirstPageFlow(scrapeUrl: String): Page =
        getPage(scrapeUrl) {
            it.htmlDocument {
                val jsonData =
                    script {
                        withId = "__NEXT_DATA__"
                        findFirst { html }
                    }

                json
                    .decodeFromString<NikeResponse>(
                        jsonData,
                    ).props.pageProps.initialState.wall
            }
        }

    private suspend fun createNextPageFlow(next: String): Page =
        getPage(next) {
            json.decodeFromString<Wall>(it.responseBody)
        }

    private suspend fun getPage(
        scrapeUrl: String,
        extractWall: (it.skrape.fetcher.Result) -> Wall,
    ): Page {
        val proxyInfo = randomProxy?.invoke()

        val nikeResponse =
            skrape(HttpFetcher) {
                request {
                    url = scrapeUrl
                    sslRelaxed = true
                    timeout =
                        HTTP_REQUEST_TIMEOUT
                    headers = mapOf("nike-api-caller-id" to "com.nike:commerce.idpdp.mobile")
                    proxyInfo?.let {
                        proxy = ProxyBuilder(host = it.address, port = it.port)
                    }
                    authentication =
                        basic {
                            username = proxyInfo?.username.orEmpty()
                            password = proxyInfo?.password.orEmpty()
                        }
                }
                extractIt<NikeSkrapeResponse> { result ->
                    result.wall = extractWall(this)
                }
            }

        val items = mutableListOf<Item>()
        nikeResponse.wall.productGroupings.forEach { productGrouping ->
            productGrouping.products?.forEach { product ->
                items.add(product.toItem())
            }
        }

        // The first page nikeResponse.wall.pageData.next is filled, all subsequent pages nikeResponse.wall.pages.next is filled.
        val nextPagePath =
            nikeResponse.wall.pages.next
                .ifEmpty { nikeResponse.wall.pageData.next }
        if (nextPagePath.isNotEmpty()) {
            return Page("https://api.nike.com/$nextPagePath", items)
        } else {
            return Page(null, items)
        }
    }

    private fun Product.toItem(): Item =
        Item(
            id = this.globalProductId,
            url = this.pdpUrl.url,
            name = this.copy.title,
            description = null,
            imageUrl = null,
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

    companion object {
        const val HTTP_REQUEST_TIMEOUT = 5000
    }
}
