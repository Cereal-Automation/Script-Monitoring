package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.data.common.useragent.DESKTOP_USER_AGENTS
import com.cereal.command.monitor.data.common.webclient.defaultJSoupClient
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import org.jsoup.nodes.Document
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FundaItemRepository(
    private val cities: List<String>,
    private val maxPrice: Int?,
    private val minSizeM2: Int?,
    private val minRooms: Int?,
    private val logRepository: LogRepository,
    private val randomProxy: RandomProxy? = null,
    private val timeout: Duration = 30.seconds,
) : ItemRepository {
    private val userAgent = DESKTOP_USER_AGENTS.random()

    override suspend fun getItems(nextPageToken: String?): Page {
        val items = mutableListOf<Item>()
        for (city in cities) {
            try {
                items += fetchCity(city)
            } catch (e: Exception) {
                logRepository.info("Funda: failed to fetch listings for city '$city': ${e.message}")
            }
        }
        return Page(nextPageToken = null, items = items)
    }

    private suspend fun fetchCity(city: String): List<Item> {
        val url = buildCityUrl(city)
        val response = defaultJSoupClient(url, timeout, randomProxy?.invoke(), userAgent).execute()
        if (response.statusCode() != 200) {
            logRepository.info("Funda: HTTP ${response.statusCode()} for '$city' at $url")
            return emptyList()
        }
        val document = response.parse()
        val links =
            document
                .select("a[data-object-url-tracking]")
                .map { it.attr("abs:href") }
                .filter { it.isNotBlank() }
                .distinct()

        if (links.isEmpty()) {
            logRepository.info("Funda: no listings found for city '$city' at $url")
        }

        return links.mapNotNull { listingUrl ->
            try {
                fetchListing(listingUrl, city)
            } catch (e: Exception) {
                logRepository.info("Funda: failed to fetch listing '$listingUrl': ${e.message}")
                null
            }
        }
    }

    private suspend fun fetchListing(
        url: String,
        city: String,
    ): Item? {
        val response = defaultJSoupClient(url, timeout, randomProxy?.invoke(), userAgent).execute()
        if (response.statusCode() != 200) {
            logRepository.info("Funda: HTTP ${response.statusCode()} for listing '$url'")
            return null
        }
        val doc = response.parse()

        val title =
            doc.selectFirst("h1.object-header__title")?.text()?.trim()
                ?: doc.title().substringBefore(" - ").trim()
        val address = doc.selectFirst("span.object-header__subtitle")?.text()?.trim() ?: ""
        val rawPrice = doc.selectFirst("strong.object-header__price")?.text()?.trim() ?: ""
        val rawSize = kenmerkenValue(doc, "Woonoppervlak")
        val rawRooms = kenmerkenValue(doc, "Aantal kamers")
        val available = kenmerkenValue(doc, "Aanvaarding")
        val energyLabel = doc.select("span.energielabel, div.energielabel").firstOrNull()?.text()?.trim() ?: ""
        val offeredSince = kenmerkenValue(doc, "Aangeboden sinds")

        val price = parsePrice(rawPrice)
        val sizeM2 = parseSizeM2(rawSize)
        val rooms = parseRooms(rawRooms)

        if (!passesFilters(price, sizeM2, rooms)) return null

        return Item(
            id = url,
            url = url,
            name = "$title · ${city.replaceFirstChar { it.uppercase() }}",
            description = address,
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

    private fun kenmerkenValue(
        doc: Document,
        key: String,
    ): String {
        val dt =
            doc.select("dl.object-kenmerken-list dt")
                .firstOrNull { it.text().contains(key, ignoreCase = true) }
        return dt?.nextElementSibling()?.text()?.trim() ?: ""
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

    private fun buildCityUrl(city: String): String =
        if (maxPrice != null) {
            "https://www.funda.nl/huur/$city/?prijsmax=$maxPrice"
        } else {
            "https://www.funda.nl/huur/$city/"
        }

    companion object {
        fun parsePrice(raw: String): BigDecimal? {
            if (raw.isBlank()) return null
            val cleaned =
                raw
                    .replace("€", "")
                    .replace(".", "")
                    .replace("/maand", "", ignoreCase = true)
                    .replace(",", ".")
                    .trim()
            return cleaned.toBigDecimalOrNull()
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
