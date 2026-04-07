package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import dev.kdriver.core.browser.Browser
import dev.kdriver.core.browser.createBrowser
import dev.kdriver.core.tab.ReadyState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import java.math.BigDecimal
import java.util.NoSuchElementException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ParariusItemRepository(
    private val cities: List<String>,
    private val maxPrice: Int?,
    private val minSizeM2: Int?,
    private val minRooms: Int?,
    private val furnishing: Furnishing? = null,
    private val propertyType: PropertyType? = null,
    private val randomProxy: RandomProxy? = null,
    private val logRepository: LogRepository,
    private val timeout: Duration = 30.seconds,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?): Page {
        val items = mutableListOf<Item>()
        val browserScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val browser = createBrowser(browserScope) { headless = true }

        try {
            for (city in cities) {
                try {
                    items += fetchCity(city, browser)
                } catch (e: Exception) {
                    logRepository.info("Pararius: failed to fetch listings for city '$city': ${e.message}")
                }
            }
        } finally {
            runCatching { browser.stop() }
            browserScope.coroutineContext[Job]?.cancel()
        }

        return Page(nextPageToken = null, items = items)
    }

    private suspend fun fetchCity(
        city: String,
        browser: Browser,
    ): List<Item> {
        val url = buildCityUrl(city)
        val html = fetchWithBrowser(url, browser)
        val document = Jsoup.parse(html, url)

        val links =
            document
                .select("section.listing-search-item a.listing-search-item__link--title")
                .map { it.attr("abs:href") }
                .filter { it.isNotBlank() }
                .distinct()

        if (links.isEmpty()) {
            logRepository.info("Pararius: no listings found for city '$city' at $url")
        }

        return links.mapNotNull { listingUrl ->
            try {
                fetchListing(listingUrl, city, browser)
            } catch (e: Exception) {
                logRepository.info("Pararius: failed to fetch listing '$listingUrl': ${e.message}")
                null
            }
        }
    }

    private suspend fun fetchWithBrowser(
        url: String,
        browser: Browser,
    ): String {
        var pageContent: String? = null
        for (i in 1..5) {
            try {
                val page = browser.get(url)
                page.waitForReadyState(ReadyState.COMPLETE, timeout = 10000)
                pageContent = page.getContent()
                break
            } catch (e: NoSuchElementException) {
                delay(500)
            }
        }
        if (pageContent == null) throw IllegalStateException("Could not get page from browser")
        return pageContent
    }

    private suspend fun fetchListing(
        url: String,
        city: String,
        browser: Browser,
    ): Item? {
        val html = fetchWithBrowser(url, browser)
        val doc = Jsoup.parse(html, url)

        val rawTitle = doc.selectFirst("h1.listing-detail-summary__title")?.text()?.trim() ?: ""
        val title = rawTitle.removePrefix("For rent:").trim()
        val address = doc.selectFirst("div.listing-detail-summary__location")?.text()?.trim() ?: ""
        val rawPrice = doc.selectFirst("span.listing-detail-summary__price-main")?.text()?.trim() ?: ""
        val rawSize = doc.selectFirst("li.illustrated-features__item--surface-area")?.text()?.trim() ?: ""
        val rawRooms = doc.selectFirst("li.illustrated-features__item--number-of-rooms")?.text()?.trim() ?: ""
        val available = doc.selectFirst("dd.listing-features__description--acceptance")?.text()?.trim() ?: ""
        val energyLabel =
            doc
                .select("dd[class*=listing-features__description--energy-label]")
                .firstOrNull()?.text()?.trim() ?: ""
        val offeredSince = doc.selectFirst("dd.listing-features__description--offered_since")?.text()?.trim() ?: ""

        val price = parsePrice(rawPrice)
        val sizeM2 = parseSizeM2(rawSize)
        val rooms = parseRooms(rawRooms)
        val imageUrl = doc.selectFirst("meta[property=og:image]")?.attr("content")?.trim()

        if (!passesFilters(price, sizeM2, rooms)) return null

        return Item(
            id = url,
            url = url,
            name = "$title · ${city.replaceFirstChar { it.uppercase() }}",
            description = address,
            imageUrl = imageUrl,
            properties =
                buildList {
                    price?.let { add(ItemProperty.Price(it, Currency.EUR)) }
                    sizeM2?.let { add(ItemProperty.Custom("size_m2", "$it m²")) }
                    rooms?.let { add(ItemProperty.Custom("rooms", it.toString())) }
                    if (available.isNotBlank()) add(ItemProperty.Custom("available", available))
                    if (energyLabel.isNotBlank()) add(ItemProperty.Custom("energy_label", energyLabel))
                    if (offeredSince.isNotBlank()) add(ItemProperty.Custom("offered_since", offeredSince))
                },
        )
    }

    internal fun passesFilters(
        price: BigDecimal?,
        sizeM2: Int?,
        rooms: Int?,
    ): Boolean {
        if (maxPrice != null && price != null && price > maxPrice.toBigDecimal()) return false
        if (minSizeM2 != null && sizeM2 != null && sizeM2 < minSizeM2) return false
        if (minRooms != null && rooms != null && rooms < minRooms) return false
        return true
    }

    private fun buildCityUrl(city: String): String {
        val segments =
            buildList<String> {
                add("https://www.pararius.com/apartments/$city")
                propertyType?.parariusSegment?.let { add(it) }
                furnishing?.let { add(it.parariusSegment) }
                maxPrice?.let { add("0-$it") }
            }
        return segments.joinToString("/")
    }

    companion object {
        fun parsePrice(raw: String): BigDecimal? {
            if (raw.isBlank()) return null
            val digits = raw.replace(Regex("[^\\d]"), "")
            return digits.toBigDecimalOrNull()
        }

        fun parseSizeM2(raw: String): Int? {
            if (raw.isBlank()) return null
            return Regex("""\d+""").find(raw)?.value?.toIntOrNull()
        }

        fun parseRooms(raw: String): Int? {
            if (raw.isBlank()) return null
            return raw.trim().split(" ").firstOrNull()?.toIntOrNull()
        }
    }
}
