# Rental Monitor — Design Spec

**Date:** 2026-03-28  
**Status:** Approved  
**Scope:** New Kotlin module `script-rental` + two `ItemRepository` implementations in `command-monitoring`

---

## Overview

A rental monitor for the Dutch market that scrapes Pararius and Funda for new rental listings and sends Discord notifications via the existing framework. Modelled on the reference Python project at `Downloads/rental-monitor-main` but implemented as a native Kotlin module consistent with all other scripts in this monorepo.

---

## Module Structure

```
script-rental/
├── build.gradle.kts
└── src/main/
    ├── kotlin/com/cereal/rental/
    │   ├── RentalScript.kt
    │   └── RentalConfiguration.kt
    └── resources/
        └── manifest.json

command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rental/
├── ParariusItemRepository.kt
└── FundaItemRepository.kt
```

`settings.gradle.kts` must include `"script-rental"`.

---

## Configuration

```kotlin
interface RentalConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = "cities",
        name = "Cities",
        description = "Comma-separated city names, e.g. amsterdam,rotterdam,utrecht",
        isScriptIdentifier = true
    )
    fun cities(): String

    @ScriptConfigurationItem(
        keyName = "max_price",
        name = "Max Price (EUR/month)",
        description = "Maximum monthly rent in EUR. Leave empty for no limit."
    )
    fun maxPrice(): Int?

    @ScriptConfigurationItem(
        keyName = "min_size_m2",
        name = "Min Size (m²)",
        description = "Minimum apartment size in square metres. Leave empty for no limit."
    )
    fun minSizeM2(): Int?

    @ScriptConfigurationItem(
        keyName = "min_rooms",
        name = "Min Rooms",
        description = "Minimum number of rooms. Leave empty for no limit."
    )
    fun minRooms(): Int?

    @ScriptConfigurationItem(
        keyName = "enable_pararius",
        name = "Enable Pararius",
        description = "Scrape Pararius.com for new listings."
    )
    fun enablePararius(): Boolean

    @ScriptConfigurationItem(
        keyName = "enable_funda",
        name = "Enable Funda",
        description = "Scrape Funda.nl for new listings."
    )
    fun enableFunda(): Boolean
}
```

Filter logic: listings that fail `maxPrice`, `minSizeM2`, or `minRooms` checks are excluded inside the repository before items are returned. `null` config values mean "no filter."

---

## ItemRepository Design

### Shared approach (both repositories)

- Use `defaultJSoupClient` (OkHttp + JSoup) — same client as `ZalandoItemRepository`
- Use `UserAgents` utility for random user-agent rotation
- Constructor takes: `cities: List<String>`, `maxPrice: Int?`, `minSizeM2: Int?`, `minRooms: Int?`, `logRepository: LogRepository`
- `getItems(nextPageToken: String?)` iterates all cities, fetches listing-page HTML, extracts listing URLs, then fetches each listing detail page
- Returns `Page(nextPageToken = null, items = items)` — one shot, no pagination
- HTTP errors per city/listing are caught, logged as warnings, and skipped — the run continues

### ParariusItemRepository

**Listing page URL:**
```
https://www.pararius.com/apartments/{city}/0-{maxPrice}
```
`maxPrice` segment omitted if config is null → `https://www.pararius.com/apartments/{city}`

**CSS selector for listing links:** `section.listing-search-item h2.listing-search-item__title a`

**Per-listing detail page — fields extracted:**

| Field | CSS / selector |
|---|---|
| Title | `h1.listing-detail-summary__title` |
| Address | `div.listing-detail-summary__location` |
| Price | `dd.listing-features__description--for_rent_price` |
| Size | `li.illustrated-features__item--surface-area` |
| Rooms | `li.illustrated-features__item--number-of-rooms` |
| Available | `dd.listing-features__description--acceptance` |
| Energy label | `dd.listing-features__description--energy-label` (any variant) |
| Offered since | `dd.listing-features__description--offered_since` |

**Item construction:**
```
id          = URL (stable unique identifier)
name        = "{title} · {city.title()}"
url         = absolute listing URL
description = address
properties  = [Price, Custom("size_m2"), Custom("rooms"), Custom("available"),
               Custom("energy_label"), Custom("offered_since")]
```

### FundaItemRepository

**Listing page URL:**
```
https://www.funda.nl/huur/{city}/
```
With optional price filter appended as query param `?prijsmax={maxPrice}`.

**CSS selector for listing links:** `a[data-object-url-tracking]` (links to individual listing pages)

**Per-listing detail page — fields extracted:**

| Field | Method |
|---|---|
| Title | `h1.object-header__title` or `<title>` tag |
| Address | `span.object-header__subtitle` |
| Price | `strong.object-header__price` |
| Size | Parse from `<ul class="object-kenmerken-list">` key "Woonoppervlak" |
| Rooms | Parse from same list, key "Aantal kamers" |
| Available | Key "Aanvaarding" |
| Energy label | `span.energielabel` |
| Offered since | Key "Aangeboden sinds" |

**Note:** Funda may return Dutch text. No translation step — raw Dutch values are stored and displayed as-is in Discord notifications.

---

## Item Model (mapped from scraped data)

```kotlin
Item(
    id          = listingUrl,
    name        = "${title} · ${city.replaceFirstChar { it.uppercase() }}",
    url         = listingUrl,
    description = address,
    properties  = buildList {
        priceEur?.let { add(ItemProperty.Price(it, Currency.EUR)) }
        sizeM2?.let   { add(ItemProperty.Custom("size_m2", it)) }
        rooms?.let    { add(ItemProperty.Custom("rooms", it)) }
        available?.let { add(ItemProperty.Custom("available", it)) }
        energyLabel?.let { add(ItemProperty.Custom("energy_label", it)) }
        offeredSince?.let { add(ItemProperty.Custom("offered_since", it)) }
    }
)
```

---

## Script Class

```kotlin
class RentalScript : Script<RentalConfiguration> {
    private val commandExecutionScript = CommandExecutionScript(
        scriptId = "com.cereal-automation.monitor.rental",
        scriptPublicKey = null,
    )

    override suspend fun execute(configuration, provider, statusUpdate): ExecutionResult {
        val factory = MonitorCommandFactory(provider)
        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Dutch Rental Monitor")

        val cities = configuration.cities().split(",").map { it.trim() }.filter { it.isNotBlank() }
        val strategy = MonitorStrategyFactory.newItemAvailableMonitorStrategy(Clock.System.now())

        val commands = buildList {
            if (configuration.enablePararius()) {
                add(factory.monitorCommand(
                    ParariusItemRepository(cities, configuration.maxPrice(), configuration.minSizeM2(), configuration.minRooms(), logRepository),
                    logRepository, notificationRepository, listOf(strategy),
                    configuration.monitorInterval()?.seconds
                ))
            }
            if (configuration.enableFunda()) {
                add(factory.monitorCommand(
                    FundaItemRepository(cities, configuration.maxPrice(), configuration.minSizeM2(), configuration.minRooms(), logRepository),
                    logRepository, notificationRepository, listOf(strategy),
                    configuration.monitorInterval()?.seconds
                ))
            }
        }

        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }
}
```

---

## Filter Logic (in ItemRepository)

After scraping all listings for a city, apply client-side filters before adding to results:

```kotlin
fun passesFilters(price: BigDecimal?, sizeM2: Int?, rooms: Int?): Boolean {
    if (maxPrice != null && price != null && price > maxPrice.toBigDecimal()) return false
    if (minSizeM2 != null && sizeM2 != null && sizeM2 < minSizeM2) return false
    if (minRooms != null && rooms != null && rooms < minRooms) return false
    return true
}
```

If a required field (price/size/rooms) cannot be parsed but the config filter is set, the listing is **included** (conservative — better to notify than silently drop).

---

## Error Handling

| Failure scenario | Behaviour |
|---|---|
| HTTP error on listing page (city-level) | Log warning, skip city, continue to next |
| HTTP error on individual listing detail | Log warning, skip listing, continue to next |
| CSS selector returns no results | Log warning, return empty list for that city |
| Field parse error (price, size, rooms) | Treat as null, include if filter is not set; include if filter is set (conservative) |
| All repositories throw | `commandExecutionScript` propagates failure as `ExecutionResult.Failure` |

Rate limiting: `defaultJSoupClient` already wraps OkHttp with `RateLimiterPlugin` from `command-monitoring/data/common/httpclient/`.

---

## Deduplication & Notifications

Identical to all other modules:
- In-memory via `MonitorStatus.monitorItems: Map<String, Item>` in `ChainContext`
- First run after restart: baseline established, no notifications sent (strategy `requiresBaseline() = true`)
- Subsequent runs: new listing URL → notification sent to Discord
- Notifications include: listing name, address, price, URL, size, rooms, energy label

---

## Build Configuration

`script-rental/build.gradle.kts`:
```kotlin
plugins { kotlin("jvm") }
dependencies {
    compileOnly(libs.cereal.sdk) { artifact { classifier = "all" } }
    implementation(libs.bundles.cereal.base)
    implementation(project(":script-common"))
    implementation(project(":command"))
    implementation(project(":command-monitoring"))
    testImplementation(kotlin("test"))
}
tasks.test { useJUnitPlatform() }
kotlin { jvmToolchain(17) }
```

`manifest.json`:
```json
{
  "package_name": "com.cereal-automation.monitor.rental",
  "name": "Dutch Rental Monitor",
  "version_code": 1,
  "script": "com.cereal.rental.RentalScript"
}
```

---

## Out of Scope

- Persistent storage (Sheety/database) — in-memory dedup matches all other modules
- Email notifications — Discord used (existing framework)
- Translation of Dutch text — raw Dutch values displayed in Discord
- Proxy rotation — not configured initially (can be added like Zalando if blocking becomes an issue)
- Pagination beyond the first search results page — first page only (same as reference project)
