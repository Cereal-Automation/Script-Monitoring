package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.script.repository.LogRepository
import dev.kdriver.core.browser.Browser
import org.jsoup.Jsoup
import java.math.BigDecimal

class FundaItemRepository(
    cities: List<String>,
    private val furnishing: Furnishing? = null,
    private val propertyType: PropertyType? = null,
    logRepository: LogRepository,
) : BrowserBasedItemRepository(cities, logRepository) {
    override val name: String = "Funda"

    override suspend fun fetchCity(
        city: String,
        browser: Browser,
    ): List<Item> {
        val url = buildCityUrl(city)
        val html = fetchWithBrowser(url, browser)
        val document = Jsoup.parse(html, url)

        val jsonLd = document.selectFirst("script[data-hid=result-list-metadata]")?.data()
        val jsonLinks =
            if (jsonLd != null) {
                Regex(""""url":"([^"]+)"""").findAll(jsonLd).map { it.groupValues[1] }.toList()
            } else {
                emptyList()
            }

        val domLinks =
            document
                .select("a[data-object-url-tracking]")
                .map { it.attr("abs:href") }

        val links =
            (jsonLinks + domLinks)
                .filter { it.isNotBlank() && it.contains("/detail/") }
                .distinct()

        if (links.isEmpty()) {
            logRepository.info("Funda: no listings found for city '$city' at $url")
            return emptyList()
        }

        logRepository.info("Funda: fetching ${links.size} listing(s) for city '$city'")

        return links.mapIndexedNotNull { index, listingUrl ->
            if (index > 0 && index % LISTING_PROGRESS_EVERY == 0) {
                logRepository.info("Funda: processed $index/${links.size} listings for '$city'")
            }
            try {
                fetchListing(listingUrl, city, browser)
            } catch (e: Exception) {
                logRepository.info("Funda: failed to fetch listing '$listingUrl': ${e.message}")
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

        val title =
            doc.selectFirst("h1.object-header__title")?.text()?.trim()
                ?: doc.title().substringBefore(" - ").trim()

        if (title.contains("Je bent bijna op de pagina die je zoekt", ignoreCase = true)) {
            logRepository.info("Funda: Bot protection blocked listing '$url'")
            return null
        }

        // Funda migrated to Nuxt 3 with randomized tailwind classes.
        // Old CSS selectors like 'object-header__subtitle' no longer exist in the DOM.
        // We parse values directly from the raw HTML/JSON string payloads that are present in the SSR response.
        val address = title.substringAfter(": ").substringBefore(" |").trim()
        val rawPrice =
            Regex("""€\s*([\d.,]+)\s*(?:/mnd|per maand)""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.get(1)?.trim() ?: ""
        val rawSize =
            Regex("""(\d+)\s*m²""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.get(1)?.trim() ?: ""
        val rawRooms =
            Regex("""(\d+)\s*(?:kamer|kamers|slaapkamers?)""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.get(1)?.trim() ?: ""
        val available =
            Regex("""Aanvaarding["',:\s]+([^"'{]+)""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.get(1)?.trim() ?: ""
        val energyLabel =
            Regex("""Energielabel["',:\s]+([A-G][\+\d]{0,3})(?=["',:\s])""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.get(1)?.trim() ?: ""
        val offeredSince =
            Regex("""Aangeboden sinds["',:\s]+([^"'{]+)""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.get(1)?.trim() ?: ""

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
                    add(ItemProperty.Custom("Source", "Funda"))
                },
        )
    }

    private fun buildCityUrl(city: String): String {
        val segments =
            buildList<String> {
                add("https://www.funda.nl/huur/$city")
                propertyType?.let { add(it.fundaSegment) }
                furnishing?.let { add(it.fundaSegment) }
            }
        return segments.joinToString("/") + "/"
    }
}
