package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.script.repository.LogRepository
import dev.kdriver.core.browser.Browser
import org.jsoup.Jsoup

class ParariusItemRepository(
    cities: List<String>,
    private val furnishing: Furnishing? = null,
    private val propertyType: PropertyType? = null,
    logRepository: LogRepository,
) : BrowserBasedItemRepository(cities, logRepository) {
    override val name: String = "Pararius"

    override suspend fun fetchCity(
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
            return emptyList()
        }

        logRepository.info("Pararius: fetching ${links.size} listing(s) for city '$city'")

        return links.mapIndexedNotNull { index, listingUrl ->
            if (index > 0 && index % LISTING_PROGRESS_EVERY == 0) {
                logRepository.info("Pararius: processed $index/${links.size} listings for '$city'")
            }
            try {
                fetchListing(listingUrl, city, browser)
            } catch (e: Exception) {
                logRepository.info("Pararius: failed to fetch listing '$listingUrl': ${e.message}")
                null
            }
        }
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

        return Item(
            id = url,
            url = url,
            name = "$title · ${city.replaceFirstChar { it.uppercase() }}",
            description = address,
            imageUrl = imageUrl,
            properties =
                buildList {
                    price?.let { add(ItemProperty.Price(it, Currency.EUR)) }
                    sizeM2?.let { add(ItemProperty.Custom("Size", "$it m²")) }
                    rooms?.let { add(ItemProperty.Custom("Rooms", it.toString())) }
                    if (available.isNotBlank()) add(ItemProperty.Custom("Available", available))
                    if (energyLabel.isNotBlank()) add(ItemProperty.Custom("Energy label", energyLabel))
                    if (offeredSince.isNotBlank()) add(ItemProperty.Custom("Offered since", offeredSince))
                    add(ItemProperty.Custom("Source", "Pararius"))
                },
        )
    }

    private fun buildCityUrl(city: String): String {
        val segments =
            buildList<String> {
                add("https://www.pararius.com/apartments/$city")
                propertyType?.parariusSegment?.let { add(it) }
                furnishing?.let { add(it.parariusSegment) }
            }
        return segments.joinToString("/")
    }
}
