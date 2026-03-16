# RSS Filtering Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Extend `script-rss` to allow users to filter new RSS items based on keywords, authors, and categories using configurable MATCH_ALL or MATCH_ANY logic.

**Architecture:** 
1. `RssFeedItemRepository` will extract `author` and `categories` from `RssItem` into `ItemProperty.Custom`.
2. `RssConfiguration` will gain four new fields (keywords, authors, categories, filter_logic).
3. `FilteredNewItemMonitorStrategy` will wrap `NewItemAvailableMonitorStrategy` (composition) to perform the baseline newness check, and then evaluate the custom filters using the selected logic.
4. `RssScript` will instantiate the new strategy instead of the standard new item strategy.

**Tech Stack:** Kotlin JVM 17, Gradle Kotlin DSL, Cereal SDK.

---

## Chunk 1: Data Extraction

### Task 1: Update RssFeedItemRepository

We need to extract `author` and `categories` from the `RssItem` and map them to `ItemProperty.Custom`. Since `categories` is a `List<String>`, we will create one `ItemProperty.Custom("category", it)` for each category string.

**Files:**
- Modify: `command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rss/RssFeedItemRepository.kt:33-40`

- [ ] **Step 1: Write the failing test**
Update `command-monitoring/src/test/kotlin/com/cereal/command/monitor/data/rss/RssFeedItemRepositoryTest.kt`. If it exists, add an assertion for `ItemProperty.Custom`. If no test exists, skip this step.

- [ ] **Step 2: Update the repository**

Modify `getItems` in `RssFeedItemRepository.kt`:

```kotlin
                if (id != null && url != null && name != null) {
                    val values = mutableListOf<ItemProperty>()
                    getPublishDate(it)?.let { date -> values.add(date) }
                    
                    it.author?.let { author ->
                        if (author.isNotBlank()) {
                            values.add(ItemProperty.Custom("author", author))
                        }
                    }
                    
                    it.categories.forEach { category ->
                        if (category.isNotBlank()) {
                            values.add(ItemProperty.Custom("category", category))
                        }
                    }

                    Item(id, url, name, description = it.description, imageUrl = it.image, properties = values)
                } else {
```

- [ ] **Step 3: Compile and verify**

Run: `./gradlew :command-monitoring:compileKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add command-monitoring/src/main/kotlin/com/cereal/command/monitor/data/rss/RssFeedItemRepository.kt
git commit -m "feat: extract author and categories from rss feed"
```

---

## Chunk 2: Configuration

### Task 2: Create FilterLogic Enum & Update Configuration

Add the new configuration properties to `RssConfiguration`.

**Files:**
- Create: `script-rss/src/main/kotlin/com/cereal/rss/FilterLogic.kt`
- Modify: `script-rss/src/main/kotlin/com/cereal/rss/RssConfiguration.kt`

- [ ] **Step 1: Create the FilterLogic enum**

Create `script-rss/src/main/kotlin/com/cereal/rss/FilterLogic.kt`:

```kotlin
package com.cereal.rss

enum class FilterLogic {
    MATCH_ANY,
    MATCH_ALL
}
```

- [ ] **Step 2: Update RssConfiguration**

Modify `RssConfiguration.kt` to add the 4 new items and their constant keys:

```kotlin
    @ScriptConfigurationItem(
        keyName = KEY_FILTER_KEYWORDS,
        name = "Keywords",
        description = "Comma-separated list of keywords to search for in title or description.",
    )
    fun filterKeywords(): String?

    @ScriptConfigurationItem(
        keyName = KEY_FILTER_AUTHORS,
        name = "Authors",
        description = "Comma-separated list of exact author names to match.",
    )
    fun filterAuthors(): String?

    @ScriptConfigurationItem(
        keyName = KEY_FILTER_CATEGORIES,
        name = "Categories",
        description = "Comma-separated list of categories to match.",
    )
    fun filterCategories(): String?

    @ScriptConfigurationItem(
        keyName = KEY_FILTER_LOGIC,
        name = "Filter Logic",
        description = "MATCH_ALL (AND) or MATCH_ANY (OR). Defaults to MATCH_ANY.",
    )
    fun filterLogic(): FilterLogic?

    companion object {
        const val KEY_RSS_URL = "rss_url"
        const val KEY_MONITOR_NEW_ITEMS = "monitor_new_items"
        const val KEY_FILTER_KEYWORDS = "filter_keywords"
        const val KEY_FILTER_AUTHORS = "filter_authors"
        const val KEY_FILTER_CATEGORIES = "filter_categories"
        const val KEY_FILTER_LOGIC = "filter_logic"
    }
```

- [ ] **Step 3: Compile and verify**

Run: `./gradlew :script-rss:compileKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add script-rss/src/main/kotlin/com/cereal/rss/
git commit -m "feat: add filtering configuration options"
```

---

## Chunk 3: Strategy & Script Wiring

### Task 3: Create FilteredNewItemMonitorStrategy

Create the strategy that composes the new item check and evaluates filters.

**Files:**
- Create: `script-rss/src/main/kotlin/com/cereal/rss/FilteredNewItemMonitorStrategy.kt`
- Create: `script-rss/src/test/kotlin/com/cereal/rss/FilteredNewItemMonitorStrategyTest.kt`

- [ ] **Step 1: Write the strategy**

Create `script-rss/src/main/kotlin/com/cereal/rss/FilteredNewItemMonitorStrategy.kt`:

```kotlin
package com.cereal.rss

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.strategy.MonitorStrategy

class FilteredNewItemMonitorStrategy(
    private val baselineStrategy: MonitorStrategy,
    private val keywords: List<String>,
    private val authors: List<String>,
    private val categories: List<String>,
    private val logic: FilterLogic,
) : MonitorStrategy {

    override suspend fun shouldNotify(item: Item, previousItem: Item?): String? {
        // Delegate baseline newness check
        val baselineMessage = baselineStrategy.shouldNotify(item, previousItem) ?: return null
        
        // If no filters configured, behave identically to original
        if (keywords.isEmpty() && authors.isEmpty() && categories.isEmpty()) {
            return baselineMessage
        }

        val matchesKeyword = keywords.isNotEmpty() && keywords.any { keyword ->
            item.name.contains(keyword, ignoreCase = true) || 
            (item.description?.contains(keyword, ignoreCase = true) == true)
        }

        val itemAuthor = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "author" }?.value
        val matchesAuthor = authors.isNotEmpty() && authors.any { it.equals(itemAuthor, ignoreCase = true) }

        val itemCategories = item.properties.filterIsInstance<ItemProperty.Custom>().filter { it.name == "category" }.map { it.value }
        val matchesCategory = categories.isNotEmpty() && categories.any { configuredCategory ->
            itemCategories.any { it.equals(configuredCategory, ignoreCase = true) }
        }

        val hasKeywordFilter = keywords.isNotEmpty()
        val hasAuthorFilter = authors.isNotEmpty()
        val hasCategoryFilter = categories.isNotEmpty()

        val passed = when (logic) {
            FilterLogic.MATCH_ANY -> matchesKeyword || matchesAuthor || matchesCategory
            FilterLogic.MATCH_ALL -> {
                (!hasKeywordFilter || matchesKeyword) &&
                (!hasAuthorFilter || matchesAuthor) &&
                (!hasCategoryFilter || matchesCategory)
            }
        }

        return if (passed) baselineMessage else null
    }

    override fun requiresBaseline(): Boolean = baselineStrategy.requiresBaseline()
}
```

- [ ] **Step 2: Write tests**

Write a comprehensive test suite for `FilteredNewItemMonitorStrategy` using JUnit 5 and `runTest` from `kotlinx.coroutines.test`, testing both MATCH_ALL and MATCH_ANY logic.
Create `script-rss/src/test/kotlin/com/cereal/rss/FilteredNewItemMonitorStrategyTest.kt` to contain these tests.

- [ ] **Step 3: Commit**

```bash
git add script-rss/src/main/kotlin/com/cereal/rss/FilteredNewItemMonitorStrategy.kt
git add script-rss/src/test/kotlin/com/cereal/rss/FilteredNewItemMonitorStrategyTest.kt
git commit -m "feat: implement FilteredNewItemMonitorStrategy"
```

### Task 4: Wire Script to Strategy

Parse the CSV configuration properties and construct the new strategy instead of the raw `NewItemAvailableMonitorStrategy`.

**Files:**
- Modify: `script-rss/src/main/kotlin/com/cereal/rss/RssScript.kt`

- [ ] **Step 1: Update RssScript**

Modify the `buildMonitorStrategies` function in `RssScript.kt`:

```kotlin
    private fun buildMonitorStrategies(configuration: RssConfiguration): List<MonitorStrategy> =
        buildList {
            if (configuration.monitorNewItems()) {
                val baseline = MonitorStrategyFactory.newItemAvailableMonitorStrategy(Clock.System.now())
                
                // Helper to safely parse CSV strings
                fun parseCsv(input: String?): List<String> =
                    input?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()

                val keywords = parseCsv(configuration.filterKeywords())
                val authors = parseCsv(configuration.filterAuthors())
                val categories = parseCsv(configuration.filterCategories())
                val logic = configuration.filterLogic() ?: FilterLogic.MATCH_ANY

                add(FilteredNewItemMonitorStrategy(baseline, keywords, authors, categories, logic))
            }
        }
```

- [ ] **Step 2: Compile and test**

Run: `./gradlew :script-rss:build`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add script-rss/src/main/kotlin/com/cereal/rss/RssScript.kt
git commit -m "feat: wire filtering into RssScript"
```
