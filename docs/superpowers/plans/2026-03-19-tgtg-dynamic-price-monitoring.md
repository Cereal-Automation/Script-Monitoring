# TGTG Dynamic Price Change Monitoring Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `PriceChangedMonitorStrategy` that notifies when a TGTG bag's price changes (in either direction) while the item is in stock, controlled by a new opt-in configuration toggle.

**Architecture:** A new `MonitorStrategy` implementation is added to `command-monitoring`, following the exact same pattern as `PriceDropMonitorStrategy`. `TgtgConfiguration` gains one new boolean config item, and `TgtgScript.buildMonitorStrategies()` is updated to accept the configuration object and conditionally include the new strategy.

**Tech Stack:** Kotlin, JUnit 5, `kotlinx-coroutines` (`runBlocking` in tests), `java.math.BigDecimal` for price comparison.

---

## File Map

| Action | Path |
|---|---|
| **Create** | `command-monitoring/src/main/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategy.kt` |
| **Create** | `command-monitoring/src/test/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategyTest.kt` |
| **Modify** | `script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgConfiguration.kt` |
| **Modify** | `script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgScript.kt` |

---

## Task 1: Create `PriceChangedMonitorStrategy` with tests (TDD)

**Files:**
- Create: `command-monitoring/src/main/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategy.kt`
- Create: `command-monitoring/src/test/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategyTest.kt`

### Step 1.1 — Write the failing tests

Create the test file at `command-monitoring/src/test/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategyTest.kt`:

```kotlin
package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PriceChangedMonitorStrategyTest {
    private val subject = PriceChangedMonitorStrategy()

    private fun itemWithPriceAndStock(
        price: BigDecimal,
        currency: Currency = Currency.EUR,
        isInStock: Boolean = true,
        amount: Int? = 3,
    ) = Item(
        id = "1",
        url = null,
        name = "Spar",
        properties =
            listOf(
                ItemProperty.Price(price, currency),
                ItemProperty.Stock(isInStock = isInStock, amount = amount, level = null),
            ),
    )

    private fun itemWithPriceOnly(
        price: BigDecimal,
        currency: Currency = Currency.EUR,
    ) = Item(
        id = "1",
        url = null,
        name = "Spar",
        properties = listOf(ItemProperty.Price(price, currency)),
    )

    @Test
    fun `returns null when previousItem is null`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, null))
        }

    @Test
    fun `returns null when price is unchanged`() =
        runBlocking {
            val item = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(item, item))
        }

    @Test
    fun `returns message with down arrow when price decreases while in stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            assertNotNull(result)
            assertTrue(result.contains("↓"), "Expected ↓ in message: $result")
            assertTrue(result.contains("Spar"), "Expected item name in message: $result")
        }

    @Test
    fun `returns message with up arrow when price increases while in stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("4.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            assertNotNull(result)
            assertTrue(result.contains("↑"), "Expected ↑ in message: $result")
            assertTrue(result.contains("Spar"), "Expected item name in message: $result")
        }

    @Test
    fun `returns null when price changes but item is out of stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"), isInStock = false, amount = 0)
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns null when current item has no price`() =
        runBlocking {
            val current =
                Item(
                    id = "1",
                    url = null,
                    name = "Spar",
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, level = null)),
                )
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns null when previous item has no price`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous =
                Item(
                    id = "1",
                    url = null,
                    name = "Spar",
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, level = null)),
                )
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns null when current item has no stock property`() =
        runBlocking {
            val current = itemWithPriceOnly(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `requiresBaseline returns true`() {
        assertTrue(subject.requiresBaseline())
    }
}
```

- [ ] **Step 1.2 — Run the tests to verify they fail**

Run from the project root:
```bash
./gradlew :command-monitoring:test --tests "com.cereal.command.monitor.strategy.PriceChangedMonitorStrategyTest" 2>&1 | tail -20
```
Expected: compilation failure — `PriceChangedMonitorStrategy` does not exist yet.

- [ ] **Step 1.3 — Create the implementation**

Create `command-monitoring/src/main/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategy.kt`:

```kotlin
package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.getValue

/**
 * Monitoring strategy that notifies when the price of an in-stock item changes in either direction.
 *
 * Designed for Dynamic Price scenarios (e.g., TGTG Belgium) where a shop may adjust bag prices
 * while stock is available. Notifies on both price increases and decreases.
 *
 * Requirements:
 * - Requires a baseline (previous item state); silent on first poll cycle.
 * - Only fires when the item is currently in stock.
 * - Suppressed when price or stock data is missing.
 */
class PriceChangedMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        if (previousItem == null) return null

        val currentPrice = item.getValue<ItemProperty.Price>() ?: return null
        val previousPrice = previousItem.getValue<ItemProperty.Price>() ?: return null

        val stock = item.getValue<ItemProperty.Stock>() ?: return null
        if (!stock.isInStock) return null

        if (currentPrice.value.compareTo(previousPrice.value) == 0) return null

        val direction = if (currentPrice.value < previousPrice.value) "↓" else "↑"
        val currency = currentPrice.currency.code

        return "Price for ${item.name} changed: ${previousPrice.value} $currency → ${currentPrice.value} $currency ($direction)"
    }

    override fun requiresBaseline(): Boolean = true
}
```

- [ ] **Step 1.4 — Run the tests to verify they pass**

```bash
./gradlew :command-monitoring:test --tests "com.cereal.command.monitor.strategy.PriceChangedMonitorStrategyTest" 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`, all 8 tests green.

- [ ] **Step 1.5 — Commit**

```bash
git add command-monitoring/src/main/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategy.kt \
        command-monitoring/src/test/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategyTest.kt
git commit -m "feat: add PriceChangedMonitorStrategy for dynamic price detection"
```

---

## Task 2: Add `notifyOnPriceChange` config item to `TgtgConfiguration`

**Files:**
- Modify: `script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgConfiguration.kt`

The current file ends with a companion object that defines key constants. Add the new config item and constant.

- [ ] **Step 2.1 — Add the config method and constant**

In `TgtgConfiguration.kt`, add a new `@ScriptConfigurationItem` function after the `minimumRating()` function (before the companion object):

```kotlin
@ScriptConfigurationItem(
    keyName = KEY_NOTIFY_ON_PRICE_CHANGE,
    name = "Notify on Price Change",
    description =
        "If enabled, sends a notification whenever the price of an available bag changes (Dynamic Price). " +
            "Only fires when the item is in stock.",
)
fun notifyOnPriceChange(): Boolean = false
```

In the companion object, add:
```kotlin
const val KEY_NOTIFY_ON_PRICE_CHANGE = "notify_on_price_change"
```

The companion object after the change should look like:
```kotlin
companion object {
    const val KEY_EMAIL = "email"
    const val KEY_LATITUDE = "latitude"
    const val KEY_LONGITUDE = "longitude"
    const val KEY_RADIUS = "radius"
    const val KEY_FAVORITES_ONLY = "favorites_only"
    const val KEY_RANDOM_PROXY = "random_proxy"
    const val KEY_MINIMUM_RATING = "minimum_rating"
    const val KEY_NOTIFY_ON_PRICE_CHANGE = "notify_on_price_change"
}
```

- [ ] **Step 2.2 — Build to verify no compilation errors**

```bash
./gradlew :script-tgtg:compileKotlin 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 2.3 — Commit**

```bash
git add script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgConfiguration.kt
git commit -m "feat: add notifyOnPriceChange config toggle to TgtgConfiguration"
```

---

## Task 3: Wire `PriceChangedMonitorStrategy` into `TgtgScript`

**Files:**
- Modify: `script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgScript.kt`

Currently `buildMonitorStrategies()` takes no parameters and returns a fixed list. It needs to accept `configuration` and conditionally include the new strategy.

- [ ] **Step 3.1 — Update `buildMonitorStrategies` and its call site**

At the top of `TgtgScript.kt`, add the import for the new strategy:
```kotlin
import com.cereal.command.monitor.strategy.PriceChangedMonitorStrategy
```

Change the `buildMonitorStrategies()` function signature and body:

**Before:**
```kotlin
@OptIn(ExperimentalTime::class)
private fun buildMonitorStrategies(): List<MonitorStrategy> =
    listOf(
        NewItemAvailableMonitorStrategy(Clock.System.now()),
        StockAvailableMonitorStrategy(notifyOnInitialRun = true),
    )
```

**After:**
```kotlin
@OptIn(ExperimentalTime::class)
private fun buildMonitorStrategies(configuration: TgtgConfiguration): List<MonitorStrategy> =
    buildList {
        add(NewItemAvailableMonitorStrategy(Clock.System.now()))
        add(StockAvailableMonitorStrategy(notifyOnInitialRun = true))
        if (configuration.notifyOnPriceChange()) add(PriceChangedMonitorStrategy())
    }
```

Update the call site in `buildCommands()`:

**Before:**
```kotlin
val monitorStrategies = buildMonitorStrategies()
```

**After:**
```kotlin
val monitorStrategies = buildMonitorStrategies(configuration)
```

- [ ] **Step 3.2 — Build to verify no compilation errors**

```bash
./gradlew :script-tgtg:compileKotlin 2>&1 | tail -20
```
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3.3 — Run full test suite**

```bash
./gradlew :command-monitoring:test :script-tgtg:test 2>&1 | tail -30
```
Expected: `BUILD SUCCESSFUL`, no test failures.

- [ ] **Step 3.4 — Commit**

```bash
git add script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgScript.kt
git commit -m "feat: wire PriceChangedMonitorStrategy into TgtgScript behind config toggle"
```

---

## Verification

After all tasks are complete, run the full build to confirm nothing is broken:

```bash
./gradlew build 2>&1 | tail -30
```
Expected: `BUILD SUCCESSFUL`.
