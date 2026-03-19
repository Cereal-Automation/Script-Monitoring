# Design: TGTG Dynamic Price Change Monitoring

**Date:** 2026-03-19  
**Status:** Approved  
**Context:** TGTG (Too Good To Go) Belgium supports "Dynamic Price" — stores can adjust bag prices at any time while stock is available. Prices may start high and drop to interesting levels. Users want to be notified whenever the price changes so they can act when the price becomes attractive.

---

## Problem

The existing TGTG script notifies when:
- A new item appears
- An item comes back in stock

It does not notify when the price of an already-available item changes. This is a gap for Dynamic Price scenarios, where the actionable event is a price change, not a restock.

---

## Requirements

1. Detect when the purchase price (`itemPrice`) of a TGTG bag changes between polling cycles.
2. Only fire when the item is currently in stock (`itemsAvailable > 0`). Price changes on out-of-stock items are not actionable.
3. Notify on both price increases and decreases (any direction) — the user decides what is interesting.
4. Require a baseline (first poll establishes state; notifications begin from the second poll onward).
5. User must opt in via a configuration toggle (`notifyOnPriceChange`, default `false`).
6. Notification message must clearly show item name, old price, new price, and direction (↑ or ↓).

---

## Architecture

### Placement

The new strategy lives in `command-monitoring`, alongside the existing:
- `PriceDropMonitorStrategy`
- `EqualsOrBelowPriceMonitorStrategy`

This is consistent with existing patterns and keeps the strategy reusable across scripts.

### New File: `PriceChangedMonitorStrategy`

**Path:** `command-monitoring/src/main/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategy.kt`

**Implements:** `MonitorStrategy`

**Logic:**

```
shouldNotify(item, previousItem):
  1. If previousItem == null → return null (no baseline yet)
  2. currentPrice = item.getValue<ItemProperty.Price>() → return null if missing
  3. previousPrice = previousItem.getValue<ItemProperty.Price>() → return null if missing
  4. stock = item.getValue<ItemProperty.Stock>() → return null if missing or !isInStock
  5. If currentPrice.value == previousPrice.value → return null (no change)
  6. direction = if (currentPrice.value < previousPrice.value) "↓" else "↑"
  7. return "Price for ${item.name} changed: ${previousPrice.value} ${currency} → ${currentPrice.value} ${currency} (${direction})"

requiresBaseline(): true
```

**Note:** Currency is taken from `currentPrice.currency.code`. Both prices are `BigDecimal`, compared with `compareTo`.

### Configuration Change: `TgtgConfiguration`

**Path:** `script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgConfiguration.kt`

Add a new optional boolean config item:

```kotlin
@ScriptConfigurationItem(
    keyName = KEY_NOTIFY_ON_PRICE_CHANGE,
    name = "Notify on Price Change",
    description = "If enabled, sends a notification whenever the price of an available bag changes (Dynamic Price). Only fires when the item is in stock.",
)
fun notifyOnPriceChange(): Boolean = false
```

Add constant:
```kotlin
const val KEY_NOTIFY_ON_PRICE_CHANGE = "notify_on_price_change"
```

### Wiring: `TgtgScript`

**Path:** `script-tgtg/src/main/kotlin/com/cereal/tgtg/TgtgScript.kt`

Modify `buildMonitorStrategies()` to accept `configuration` and conditionally add the strategy:

```kotlin
private fun buildMonitorStrategies(configuration: TgtgConfiguration): List<MonitorStrategy> =
    buildList {
        add(NewItemAvailableMonitorStrategy(Clock.System.now()))
        add(StockAvailableMonitorStrategy(notifyOnInitialRun = true))
        if (configuration.notifyOnPriceChange()) add(PriceChangedMonitorStrategy())
    }
```

Pass `configuration` when calling `buildMonitorStrategies(configuration)` from `buildCommands`.

---

## Data Flow

```
Poll cycle N (first):
  - Fetch items from API
  - MonitorStatus.monitorItems = null → requiresBaseline strategies skipped
  - State stored: monitorItems = { itemId → Item }

Poll cycle N+1:
  - Fetch current items
  - For each item, previousItem = monitorStatus.monitorItems[item.id]
  - PriceChangedMonitorStrategy.shouldNotify(item, previousItem):
      - previousItem exists → proceed
      - Check isInStock on current item → if false, skip
      - Compare prices → if different, return notification message
  - If message returned → notificationRepository.notify(message, item)
  - Update monitorItems with current items
```

---

## Tests

**Path:** `command-monitoring/src/test/kotlin/com/cereal/command/monitor/strategy/PriceChangedMonitorStrategyTest.kt`

Test cases:

| Scenario | Expected result |
|---|---|
| No previous item (first run) | `null` |
| Same price, in stock | `null` |
| Price decreased, in stock | Non-null message containing "↓" |
| Price increased, in stock | Non-null message containing "↑" |
| Price changed, out of stock | `null` |
| Price missing on current item | `null` |
| Price missing on previous item | `null` |
| `requiresBaseline()` | `true` |

---

## Files Changed

| File | Type |
|---|---|
| `command-monitoring/.../strategy/PriceChangedMonitorStrategy.kt` | New |
| `command-monitoring/.../strategy/PriceChangedMonitorStrategyTest.kt` | New |
| `script-tgtg/.../TgtgConfiguration.kt` | Modified (add config item) |
| `script-tgtg/.../TgtgScript.kt` | Modified (wire strategy, pass config to builder) |

---

## Out of Scope

- Minimum price-change threshold (filtering noise) — not requested
- Tracking price history over time (only last vs current is compared)
- Notifying on `itemValue` (retail/original value) changes — only `itemPrice` (purchase price) is monitored
