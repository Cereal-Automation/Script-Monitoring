# Dutch Rental Monitor — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `script-rental` Kotlin module to the monorepo that monitors Pararius and Funda for new Dutch rental listings and sends Discord notifications.

**Architecture:** Two `ItemRepository` implementations (one per site) live in `command-monitoring/data/rental/`. Each iterates all configured cities, scrapes listing-index pages with JSoup, then fetches each listing detail page. A thin `RentalScript` / `RentalConfiguration` module wires them into the existing framework.

**Tech Stack:** Kotlin, JSoup (via existing `defaultJSoupClient`), Cereal SDK (`Script`, `ItemRepository`, `MonitorCommandFactory`, `MonitorStrategyFactory`), Gradle (libs.versions.toml).

---

## File Map

### New files
| Path | Purpose |
|---|---|
| `settings.gradle.kts` | Add `include("script-rental")` |
| `script-rental/build.gradle.kts` | Gradle subproject config |
| `script-rental/src/main/resources/manifest.json` | SDK manifest |
| `script-rental/src/main/kotlin/com/cereal/rental/RentalConfiguration.kt` | Config interface |
| `script-rental/src/main/kotlin/com/cereal/rental/RentalScript.kt` | Script entry point |
| `command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepository.kt` | Pararius scraper |
| `command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepository.kt` | Funda scraper |
| `command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepositoryTest.kt` | Unit tests |
| `command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepositoryTest.kt` | Unit tests |

---

## Task 1: Register the Gradle subproject

**Files:**
- Modify: `settings.gradle.kts`

- [ ] **Step 1: Add `script-rental` to settings**

Open `settings.gradle.kts` and add the include at the end of the existing includes:

```kotlin
// settings.gradle.kts  (add this line after include("script-rss"))
include("script-rental")
```

The full include block should now look like:
```kotlin
include("command")
include("command-monitoring")
include("script-common")
include("script-sample")
include("script-nike")
include("script-snkrs")
include("script-bdga-store")
include("script-tgtg")
include("script-zalando")
include("script-rss")
include("script-rental")
include("stockx-api-client")
```

- [ ] **Step 2: Commit**

```bash
git add settings.gradle.kts
git commit -m "build: register script-rental subproject"
```

---

## Task 2: Create the Gradle build file and manifest

**Files:**
- Create: `script-rental/build.gradle.kts`
- Create: `script-rental/src/main/resources/manifest.json`

- [ ] **Step 1: Create the build file**

Create `script-rental/build.gradle.kts` with this exact content (mirrors `script-zalando/build.gradle.kts`):

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(libs.cereal.sdk) {
        artifact {
            classifier = "all"
        }
    }
    implementation(libs.bundles.cereal.base)

    implementation(project(":script-common"))
    implementation(project(":command"))
    implementation(project(":command-monitoring"))

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
```

- [ ] **Step 2: Create the manifest**

Create `script-rental/src/main/resources/manifest.json`:

```json
{
  "package_name": "com.cereal-automation.monitor.rental",
  "name": "Dutch Rental Monitor",
  "version_code": 1
}
```

- [ ] **Step 3: Verify Gradle sync**

```bash
./gradlew :script-rental:tasks
```

Expected: task list for the new subproject is printed without errors.

- [ ] **Step 4: Commit**

```bash
git add script-rental/
git commit -m "build: add script-rental build files"
```

---

## Task 3: Create `RentalConfiguration`

**Files:**
- Create: `script-rental/src/main/kotlin/com/cereal/rental/RentalConfiguration.kt`

- [ ] **Step 1: Create the configuration interface**

```kotlin
// script-rental/src/main/kotlin/com/cereal/rental/RentalConfiguration.kt
package com.cereal.rental

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem

interface RentalConfiguration : BaseConfiguration {

    @ScriptConfigurationItem(
        keyName = KEY_CITIES,
        name = "Cities",
        description = "Comma-separated city names to monitor, e.g. amsterdam,rotterdam,utrecht",
        isScriptIdentifier = true,
    )
    fun cities(): String

    @ScriptConfigurationItem(
        keyName = KEY_MAX_PRICE,
        name = "Max Price (EUR/month)",
        description = "Maximum monthly rent in EUR. Leave empty for no limit.",
    )
    fun maxPrice(): Int?

    @ScriptConfigurationItem(
        keyName = KEY_MIN_SIZE_M2,
        name = "Min Size (m²)",
        description = "Minimum apartment size in square metres. Leave empty for no limit.",
    )
    fun minSizeM2(): Int?

    @ScriptConfigurationItem(
        keyName = KEY_MIN_ROOMS,
        name = "Min Rooms",
        description = "Minimum number of rooms. Leave empty for no limit.",
    )
    fun minRooms(): Int?

    @ScriptConfigurationItem(
        keyName = KEY_ENABLE_PARARIUS,
        name = "Enable Pararius",
        description = "If enabled, scrape Pararius.com for new listings.",
    )
    fun enablePararius(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_ENABLE_FUNDA,
        name = "Enable Funda",
        description = "If enabled, scrape Funda.nl for new listings.",
    )
    fun enableFunda(): Boolean

    companion object {
        const val KEY_CITIES = "cities"
        const val KEY_MAX_PRICE = "max_price"
        const val KEY_MIN_SIZE_M2 = "min_size_m2"
        const val KEY_MIN_ROOMS = "min_rooms"
        const val KEY_ENABLE_PARARIUS = "enable_pararius"
        const val KEY_ENABLE_FUNDA = "enable_funda"
    }
}
```

- [ ] **Step 2: Verify it compiles**

```bash
./gradlew :script-rental:compileKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add script-rental/src/main/kotlin/com/cereal/rental/RentalConfiguration.kt
git commit -m "feat(rental): add RentalConfiguration interface"
```

---

## Task 4: Create `ParariusItemRepository`

**Files:**
- Create: `command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepository.kt`
- Create: `command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepositoryTest.kt`

### Background

Pararius listing-index URL format:
- With max price: `https://www.pararius.com/apartments/{city}/0-{maxPrice}`
- Without max price: `https://www.pararius.com/apartments/{city}`

CSS selectors (verified against the reference Python project):
- Listing links on index page: `section.listing-search-item h2.listing-search-item__title a`
- Title on detail page: `h1.listing-detail-summary__title`
- Address: `div.listing-detail-summary__location`
- Price: `dd.listing-features__description--for_rent_price`
- Size: `li.illustrated-features__item--surface-area`
- Rooms: `li.illustrated-features__item--number-of-rooms`
- Available: `dd.listing-features__description--acceptance`
- Energy label: CSS class starts with `listing-features__description--energy-label`
- Offered since: `dd.listing-features__description--offered_since`

Price parsing: prices appear as "€ 1.250 per month" or "€1250 per month". Strip `€`, `.`, `per month`, and whitespace, then parse as `BigDecimal`.

Size parsing: values appear as "85 m²". Take the numeric prefix before `m²` or a space.

Rooms parsing: values appear as "3 rooms" or "3". Take the leading integer.

- [ ] **Step 1: Write the failing test**

Create `command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepositoryTest.kt`:

```kotlin
package com.cereal.command.monitor.data.rental

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ParariusItemRepositoryTest {

    @Test
    fun `parsePrice returns null for blank input`() {
        assertNull(ParariusItemRepository.parsePrice(""))
    }

    @Test
    fun `parsePrice parses euro price with dots and spaces`() {
        assertEquals(BigDecimal("1250"), ParariusItemRepository.parsePrice("€ 1.250 per month"))
    }

    @Test
    fun `parsePrice parses simple euro price`() {
        assertEquals(BigDecimal("950"), ParariusItemRepository.parsePrice("€950 per month"))
    }

    @Test
    fun `parseSizeM2 returns null for blank input`() {
        assertNull(ParariusItemRepository.parseSizeM2(""))
    }

    @Test
    fun `parseSizeM2 parses m2 value`() {
        assertEquals(85, ParariusItemRepository.parseSizeM2("85 m²"))
    }

    @Test
    fun `parseRooms returns null for blank input`() {
        assertNull(ParariusItemRepository.parseRooms(""))
    }

    @Test
    fun `parseRooms parses room count`() {
        assertEquals(3, ParariusItemRepository.parseRooms("3 rooms"))
    }

    @Test
    fun `parseRooms parses bare integer`() {
        assertEquals(2, ParariusItemRepository.parseRooms("2"))
    }

    @Test
    fun `passesFilters returns true when all filters null`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns false when price exceeds maxPrice`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1000,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = BigDecimal("1200"), sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns true when price is null and maxPrice is set (conservative)`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1000,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns false when size is below minSizeM2`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = 60,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = null, sizeM2 = 45, rooms = null))
    }

    @Test
    fun `passesFilters returns false when rooms below minRooms`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = 3,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = null, sizeM2 = null, rooms = 2))
    }
}
```

Also create `FakeLogRepository` in the same test directory:

```kotlin
// command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/FakeLogRepository.kt
package com.cereal.command.monitor.data.rental

import com.cereal.script.repository.LogRepository

class FakeLogRepository : LogRepository {
    override suspend fun debug(message: String) {}
    override suspend fun info(message: String) {}
    override suspend fun warn(message: String) {}
    override suspend fun error(message: String, throwable: Throwable?) {}
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
./gradlew :command-monitoring:test --tests "com.cereal.command.monitor.data.rental.ParariusItemRepositoryTest" 2>&1 | tail -20
```

Expected: compilation error — `ParariusItemRepository` does not exist yet.

- [ ] **Step 3: Implement `ParariusItemRepository`**

Create `command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepository.kt`:

```kotlin
package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.data.common.useragent.DESKTOP_USER_AGENTS
import com.cereal.command.monitor.data.common.webclient.defaultJSoupClient
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ParariusItemRepository(
    private val cities: List<String>,
    private val maxPrice: Int?,
    private val minSizeM2: Int?,
    private val minRooms: Int?,
    private val logRepository: LogRepository,
    private val timeout: Duration = 30.seconds,
) : ItemRepository {

    private val userAgent = DESKTOP_USER_AGENTS.random()

    override suspend fun getItems(nextPageToken: String?): Page {
        val items = mutableListOf<Item>()
        for (city in cities) {
            try {
                items += fetchCity(city)
            } catch (e: Exception) {
                logRepository.warn("Pararius: failed to fetch listings for city '$city': ${e.message}")
            }
        }
        return Page(nextPageToken = null, items = items)
    }

    private suspend fun fetchCity(city: String): List<Item> {
        val url = buildCityUrl(city)
        val document = defaultJSoupClient(url, timeout, null, userAgent).get()
        val links = document
            .select("section.listing-search-item h2.listing-search-item__title a")
            .map { it.attr("abs:href") }
            .filter { it.isNotBlank() }
            .distinct()

        if (links.isEmpty()) {
            logRepository.warn("Pararius: no listings found for city '$city' at $url")
        }

        return links.mapNotNull { listingUrl ->
            try {
                fetchListing(listingUrl, city)
            } catch (e: Exception) {
                logRepository.warn("Pararius: failed to fetch listing '$listingUrl': ${e.message}")
                null
            }
        }
    }

    private suspend fun fetchListing(url: String, city: String): Item? {
        val doc = defaultJSoupClient(url, timeout, null, userAgent).get()

        val rawTitle = doc.selectFirst("h1.listing-detail-summary__title")?.text()?.trim() ?: ""
        val title = rawTitle.removePrefix("For rent:").trim()
        val address = doc.selectFirst("div.listing-detail-summary__location")?.text()?.trim() ?: ""
        val rawPrice = doc.selectFirst("dd.listing-features__description--for_rent_price")?.text()?.trim() ?: ""
        val rawSize = doc.selectFirst("li.illustrated-features__item--surface-area")?.text()?.trim() ?: ""
        val rawRooms = doc.selectFirst("li.illustrated-features__item--number-of-rooms")?.text()?.trim() ?: ""
        val available = doc.selectFirst("dd.listing-features__description--acceptance")?.text()?.trim() ?: ""
        val energyLabel = doc
            .select("dd[class*=listing-features__description--energy-label]")
            .firstOrNull()?.text()?.trim() ?: ""
        val offeredSince = doc.selectFirst("dd.listing-features__description--offered_since")?.text()?.trim() ?: ""

        val price = parsePrice(rawPrice)
        val sizeM2 = parseSizeM2(rawSize)
        val rooms = parseRooms(rawRooms)

        if (!passesFilters(price, sizeM2, rooms)) return null

        return Item(
            id = url,
            url = url,
            name = "$title · ${city.replaceFirstChar { it.uppercase() }}",
            description = address,
            properties = buildList {
                price?.let { add(ItemProperty.Price(it, Currency.EUR)) }
                sizeM2?.let { add(ItemProperty.Custom("size_m2", "$it m²")) }
                rooms?.let { add(ItemProperty.Custom("rooms", it.toString())) }
                if (available.isNotBlank()) add(ItemProperty.Custom("available", available))
                if (energyLabel.isNotBlank()) add(ItemProperty.Custom("energy_label", energyLabel))
                if (offeredSince.isNotBlank()) add(ItemProperty.Custom("offered_since", offeredSince))
            },
        )
    }

    internal fun passesFilters(price: BigDecimal?, sizeM2: Int?, rooms: Int?): Boolean {
        if (maxPrice != null && price != null && price > maxPrice.toBigDecimal()) return false
        if (minSizeM2 != null && sizeM2 != null && sizeM2 < minSizeM2) return false
        if (minRooms != null && rooms != null && rooms < minRooms) return false
        return true
    }

    private fun buildCityUrl(city: String): String =
        if (maxPrice != null) {
            "https://www.pararius.com/apartments/$city/0-$maxPrice"
        } else {
            "https://www.pararius.com/apartments/$city"
        }

    companion object {
        fun parsePrice(raw: String): BigDecimal? {
            if (raw.isBlank()) return null
            val cleaned = raw
                .replace("€", "")
                .replace(".", "")
                .replace("per month", "", ignoreCase = true)
                .replace(",", ".")
                .trim()
            return cleaned.toBigDecimalOrNull()
        }

        fun parseSizeM2(raw: String): Int? {
            if (raw.isBlank()) return null
            return raw.replace("m²", "").trim().split(" ").firstOrNull()?.toIntOrNull()
        }

        fun parseRooms(raw: String): Int? {
            if (raw.isBlank()) return null
            return raw.trim().split(" ").firstOrNull()?.toIntOrNull()
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
./gradlew :command-monitoring:test --tests "com.cereal.command.monitor.data.rental.ParariusItemRepositoryTest"
```

Expected: `BUILD SUCCESSFUL` — all tests pass.

- [ ] **Step 5: Commit**

```bash
git add command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepository.kt \
        command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/ParariusItemRepositoryTest.kt \
        command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/FakeLogRepository.kt
git commit -m "feat(rental): add ParariusItemRepository with unit tests"
```

---

## Task 5: Create `FundaItemRepository`

**Files:**
- Create: `command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepository.kt`
- Create: `command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepositoryTest.kt`

### Background

Funda listing-index URL format:
- Without max price: `https://www.funda.nl/huur/{city}/`
- With max price: `https://www.funda.nl/huur/{city}/?prijsmax={maxPrice}`

CSS selectors (verified against the reference Python project and Funda's HTML):
- Listing links on index page: `a[data-object-url-tracking]` (each unique href is a listing)
- Title on detail page: `h1.object-header__title` (fallback: parse from `<title>` tag)
- Address: `span.object-header__subtitle`
- Price: `strong.object-header__price` — format: "€ 1.500 /maand"
- Size: in `dl.object-kenmerken-list`, find `dt` with text containing "Woonoppervlak", then its sibling `dd`
- Rooms: same list, `dt` containing "Aantal kamers", sibling `dd`
- Available: same list, `dt` containing "Aanvaarding", sibling `dd`
- Energy label: `span.energielabel` or `div.energielabel`
- Offered since: same list, `dt` containing "Aangeboden sinds", sibling `dd`

Price parsing: strip `€`, `/maand`, `.`, whitespace, then parse as `BigDecimal`.

Size parsing: values like "75 m²" — take leading number.

Rooms parsing: values like "3 kamers" or "3" — take leading number.

- [ ] **Step 1: Write the failing test**

Create `command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepositoryTest.kt`:

```kotlin
package com.cereal.command.monitor.data.rental

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class FundaItemRepositoryTest {

    @Test
    fun `parsePrice returns null for blank input`() {
        assertNull(FundaItemRepository.parsePrice(""))
    }

    @Test
    fun `parsePrice parses funda price format with slash maand`() {
        assertEquals(BigDecimal("1500"), FundaItemRepository.parsePrice("€ 1.500 /maand"))
    }

    @Test
    fun `parsePrice parses simple price`() {
        assertEquals(BigDecimal("950"), FundaItemRepository.parsePrice("€ 950"))
    }

    @Test
    fun `parseSizeM2 returns null for blank input`() {
        assertNull(FundaItemRepository.parseSizeM2(""))
    }

    @Test
    fun `parseSizeM2 parses m2 value`() {
        assertEquals(75, FundaItemRepository.parseSizeM2("75 m²"))
    }

    @Test
    fun `parseRooms returns null for blank input`() {
        assertNull(FundaItemRepository.parseRooms(""))
    }

    @Test
    fun `parseRooms parses Dutch room string`() {
        assertEquals(3, FundaItemRepository.parseRooms("3 kamers"))
    }

    @Test
    fun `parseRooms parses bare integer`() {
        assertEquals(2, FundaItemRepository.parseRooms("2"))
    }

    @Test
    fun `passesFilters returns true when all filters null`() {
        val repo = FundaItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns false when price exceeds maxPrice`() {
        val repo = FundaItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1200,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = BigDecimal("1500"), sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters includes listing when price is null and maxPrice is set (conservative)`() {
        val repo = FundaItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1200,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }
}
```

- [ ] **Step 2: Run to verify failure**

```bash
./gradlew :command-monitoring:test --tests "com.cereal.command.monitor.data.rental.FundaItemRepositoryTest" 2>&1 | tail -10
```

Expected: compilation error — `FundaItemRepository` does not exist yet.

- [ ] **Step 3: Implement `FundaItemRepository`**

Create `command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepository.kt`:

```kotlin
package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.data.common.useragent.DESKTOP_USER_AGENTS
import com.cereal.command.monitor.data.common.webclient.defaultJSoupClient
import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.repository.LogRepository
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
    private val timeout: Duration = 30.seconds,
) : ItemRepository {

    private val userAgent = DESKTOP_USER_AGENTS.random()

    override suspend fun getItems(nextPageToken: String?): Page {
        val items = mutableListOf<Item>()
        for (city in cities) {
            try {
                items += fetchCity(city)
            } catch (e: Exception) {
                logRepository.warn("Funda: failed to fetch listings for city '$city': ${e.message}")
            }
        }
        return Page(nextPageToken = null, items = items)
    }

    private suspend fun fetchCity(city: String): List<Item> {
        val url = buildCityUrl(city)
        val document = defaultJSoupClient(url, timeout, null, userAgent).get()
        val links = document
            .select("a[data-object-url-tracking]")
            .map { it.attr("abs:href") }
            .filter { it.isNotBlank() }
            .distinct()

        if (links.isEmpty()) {
            logRepository.warn("Funda: no listings found for city '$city' at $url")
        }

        return links.mapNotNull { listingUrl ->
            try {
                fetchListing(listingUrl, city)
            } catch (e: Exception) {
                logRepository.warn("Funda: failed to fetch listing '$listingUrl': ${e.message}")
                null
            }
        }
    }

    private suspend fun fetchListing(url: String, city: String): Item? {
        val doc = defaultJSoupClient(url, timeout, null, userAgent).get()

        val title = doc.selectFirst("h1.object-header__title")?.text()?.trim()
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
            properties = buildList {
                price?.let { add(ItemProperty.Price(it, Currency.EUR)) }
                sizeM2?.let { add(ItemProperty.Custom("size_m2", "$it m²")) }
                rooms?.let { add(ItemProperty.Custom("rooms", it.toString())) }
                if (available.isNotBlank()) add(ItemProperty.Custom("available", available))
                if (energyLabel.isNotBlank()) add(ItemProperty.Custom("energy_label", energyLabel))
                if (offeredSince.isNotBlank()) add(ItemProperty.Custom("offered_since", offeredSince))
            },
        )
    }

    /**
     * Find the value in a Funda kenmerken (features) dl list by matching dt text.
     * Returns the text of the sibling dd element, or empty string if not found.
     */
    private fun kenmerkenValue(doc: Document, key: String): String {
        val dt = doc.select("dl.object-kenmerken-list dt")
            .firstOrNull { it.text().contains(key, ignoreCase = true) }
        return dt?.nextElementSibling()?.text()?.trim() ?: ""
    }

    internal fun passesFilters(price: BigDecimal?, sizeM2: Int?, rooms: Int?): Boolean {
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
            val cleaned = raw
                .replace("€", "")
                .replace(".", "")
                .replace("/maand", "", ignoreCase = true)
                .replace(",", ".")
                .trim()
            return cleaned.toBigDecimalOrNull()
        }

        fun parseSizeM2(raw: String): Int? {
            if (raw.isBlank()) return null
            return raw.replace("m²", "").trim().split(" ").firstOrNull()?.toIntOrNull()
        }

        fun parseRooms(raw: String): Int? {
            if (raw.isBlank()) return null
            return raw.trim().split(" ").firstOrNull()?.toIntOrNull()
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
./gradlew :command-monitoring:test --tests "com.cereal.command.monitor.data.rental.FundaItemRepositoryTest"
```

Expected: `BUILD SUCCESSFUL` — all tests pass.

- [ ] **Step 5: Commit**

```bash
git add command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepository.kt \
        command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rental/FundaItemRepositoryTest.kt
git commit -m "feat(rental): add FundaItemRepository with unit tests"
```

---

## Task 6: Create `RentalScript`

**Files:**
- Create: `script-rental/src/main/kotlin/com/cereal/rental/RentalScript.kt`

- [ ] **Step 1: Create the script**

```kotlin
// script-rental/src/main/kotlin/com/cereal/rental/RentalScript.kt
package com.cereal.rental

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.rental.FundaItemRepository
import com.cereal.command.monitor.data.rental.ParariusItemRepository
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.command.monitor.strategy.MonitorStrategyFactory
import com.cereal.script.CommandExecutionScript
import com.cereal.script.commands.Command
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RentalScript : Script<RentalConfiguration> {

    private val commandExecutionScript = CommandExecutionScript(
        scriptId = "com.cereal-automation.monitor.rental",
        scriptPublicKey = null,
    )

    override suspend fun onStart(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }

    private fun buildCommands(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Dutch Rental Monitor")
        val cities = parseCities(configuration.cities())
        val strategy = buildStrategy()

        return buildList {
            if (configuration.enablePararius()) {
                add(
                    factory.monitorCommand(
                        itemRepository = ParariusItemRepository(
                            cities = cities,
                            maxPrice = configuration.maxPrice(),
                            minSizeM2 = configuration.minSizeM2(),
                            minRooms = configuration.minRooms(),
                            logRepository = logRepository,
                        ),
                        logRepository = logRepository,
                        notificationRepository = notificationRepository,
                        strategies = listOf(strategy),
                        scrapeInterval = configuration.monitorInterval()?.seconds,
                    ),
                )
            }
            if (configuration.enableFunda()) {
                add(
                    factory.monitorCommand(
                        itemRepository = FundaItemRepository(
                            cities = cities,
                            maxPrice = configuration.maxPrice(),
                            minSizeM2 = configuration.minSizeM2(),
                            minRooms = configuration.minRooms(),
                            logRepository = logRepository,
                        ),
                        logRepository = logRepository,
                        notificationRepository = notificationRepository,
                        strategies = listOf(strategy),
                        scrapeInterval = configuration.monitorInterval()?.seconds,
                    ),
                )
            }
        }
    }

    private fun buildStrategy(): MonitorStrategy =
        MonitorStrategyFactory.newItemAvailableMonitorStrategy(Clock.System.now())

    private fun parseCities(input: String): List<String> =
        input.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
}
```

- [ ] **Step 2: Verify the module compiles**

```bash
./gradlew :script-rental:compileKotlin
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Run all tests**

```bash
./gradlew :command-monitoring:test :script-rental:test
```

Expected: `BUILD SUCCESSFUL` — all tests pass.

- [ ] **Step 4: Commit**

```bash
git add script-rental/src/main/kotlin/com/cereal/rental/RentalScript.kt
git commit -m "feat(rental): add RentalScript entry point"
```

---

## Task 7: Full build verification

- [ ] **Step 1: Build the whole project**

```bash
./gradlew build
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 2: Run all tests across the project**

```bash
./gradlew test
```

Expected: `BUILD SUCCESSFUL` — no test failures.

- [ ] **Step 3: Commit if any fixes were needed**

If step 1 or 2 revealed issues, fix them, then:

```bash
git add -A
git commit -m "fix(rental): resolve build issues"
```

---

## Scraper Selector Notes

The CSS selectors in this plan are based on Pararius and Funda's HTML structure as of early 2026. Sites update their HTML periodically. If selectors return empty results:

1. Open the listing page in a browser
2. Right-click the target element → "Inspect"
3. Find the closest stable CSS class
4. Update the selector in the relevant repository

The tests cover parsing logic (which is stable). Integration with live sites should be verified manually by running the script with a real configuration and checking Discord for notifications.
