package com.cereal.script.monitoring.data.item.nike

import com.cereal.script.monitoring.data.item.nike.models.NikeResponse
import com.cereal.script.monitoring.data.item.nike.models.Product
import com.cereal.script.monitoring.data.item.nike.models.Wall
import com.cereal.script.monitoring.domain.models.Currency
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.repository.ItemRepository
import com.cereal.sdk.models.proxy.RandomProxy
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.ProxyBuilder
import it.skrape.fetcher.basic
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.script
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.json.Json
import java.io.IOException
import java.math.BigDecimal
import java.time.Instant

/**
 * Taken from https://www.trickster.dev/post/scraping-product-data-from-nike/
 *
 * 1. Fetch the product list page HTML with nested JSON. Use it to extract some initial product data and the first
 * value for endpoint parameter to start API scraping.
 * 2. Scrape product feed API by traversing all pages.
 */
class NikeApiItemRepository(
    private val category: ScrapeCategory,
    private val randomProxy: RandomProxy?,
) : ItemRepository {
    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    private val publishDates = mutableMapOf<String, Instant?>()
    private var firstRun = true
    private val defaultCurrencyCode = Currency.USD

    override suspend fun getItems(): Flow<Item> =
        flow {
            emitAll(parseFirstPage(category.url, randomProxy))
        }.retry(HTTP_REQUEST_RETRY_COUNT) { e ->
            (e is IOException).also { if (it) delay(HTTP_REQUEST_RETRY_DELAY) }
        }.onCompletion { cause ->
            if (cause == null) {
                firstRun = false
            }
        }

    private fun parseFirstPage(
        scrapeUrl: String,
        randomProxy: RandomProxy?,
    ): Flow<Item> =
        createFlow(scrapeUrl, randomProxy) {
            it.htmlDocument {
                val jsonData =
                    script {
                        withId = "__NEXT_DATA__"
                        findFirst { html }
                    }

                json
                    .decodeFromString<NikeResponse>(jsonData)
                    .props.pageProps.initialState.wall
            }
        }

    private fun createFlow(
        scrapeUrl: String,
        randomProxy: RandomProxy?,
        extractWall: (it.skrape.fetcher.Result) -> Wall,
    ): Flow<Item> =
        flow {
            val proxyInfo = randomProxy?.invoke()

            val nikeResponse =
                skrape(HttpFetcher) {
                    request {
                        url = scrapeUrl
                        sslRelaxed = true
                        timeout = HTTP_REQUEST_TIMEOUT
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

            nikeResponse.wall.productGroupings.forEach { productGrouping ->
                productGrouping.products?.forEach { product ->
                    emit(product.toItem())
                }
            }

            // The first page nikeResponse.wall.pageData.next is filled, all subsequent pages nikeResponse.wall.pages.next is filled.
            val nextPagePath =
                nikeResponse.wall.pages.next
                    .ifEmpty { nikeResponse.wall.pageData.next }
            if (nextPagePath.isNotEmpty()) {
                emitAll(
                    createFlow("https://api.nike.com/$nextPagePath", randomProxy) {
                        json.decodeFromString<Wall>(it.responseBody)
                    },
                )
            }
        }

    private fun Product.toItem(): Item =
        Item(
            id = this.globalProductId,
            url = this.pdpUrl.url,
            name = this.copy.title,
            values =
                listOf(
                    ItemValue.Price(
                        BigDecimal(this.prices.currentPrice),
                        Currency.fromCode(this.prices.currency) ?: defaultCurrencyCode,
                    ),
                    ItemValue.PublishDate(getPublishDate(this.globalProductId) ?: Instant.now()),
                ),
        )

    private fun getPublishDate(productId: String): Instant? {
        val publishDate = if (firstRun) null else Instant.now()
        return publishDates.getOrPut(productId) { publishDate }
    }

    companion object {
        const val HTTP_REQUEST_TIMEOUT = 5000
        const val HTTP_REQUEST_RETRY_COUNT = 5L
        const val HTTP_REQUEST_RETRY_DELAY = 5000L
    }
}
