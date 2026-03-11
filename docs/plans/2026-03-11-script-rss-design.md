# Design: script-rss Module

**Date:** 2026-03-11  
**Status:** Approved

## Overview

A new standalone `script-rss` Gradle module that allows users to provide an RSS feed URL and configure which monitoring strategies to enable. Follows the same conventions as `script-nike`, `script-zalando`, and other script modules in this project. Reuses the existing `RssFeedItemRepository` from `command-monitoring`.

## Module Structure

```
script-rss/
  build.gradle.kts
  src/main/kotlin/com/cereal/rss/
    RssConfiguration.kt
    RssScript.kt
  src/main/resources/
    manifest.json
```

`settings.gradle.kts` (root) will be updated to include `:script-rss`.

## Configuration (`RssConfiguration`)

Extends `BaseConfiguration` (which provides `monitorInterval()`).

| Config key              | UI Name                    | Type          | Notes                                          |
|-------------------------|----------------------------|---------------|------------------------------------------------|
| `rss_url`               | RSS Feed URL               | `String`      | `isScriptIdentifier = true`                    |
| `monitor_new_items`     | Monitor new items          | `Boolean`     | Enables `NewItemAvailableMonitorStrategy`       |
| `monitor_price_drop`    | Monitor price drops        | `Boolean`     | Enables `PriceDropMonitorStrategy`             |
| `monitor_stock_available` | Monitor stock available  | `Boolean`     | Enables `StockAvailableMonitorStrategy`        |
| `monitor_stock_changed` | Monitor stock changes       | `Boolean`     | Enables `StockChangedMonitorStrategy`          |
| `monitor_price_threshold` | Monitor price ≤ threshold | `Boolean`    | Enables `EqualsOrBelowPriceMonitorStrategy`    |
| `price_threshold`       | Price threshold            | `BigDecimal?` | Hidden unless `monitor_price_threshold = true` |

The `price_threshold` field uses a `StateModifier` to show/hide based on whether the price threshold strategy is enabled.

## Script (`RssScript`)

Follows the standard script execute pattern:

1. Create `MonitorCommandFactory(provider)`
2. Build `logRepository` and `notificationRepository("RSS Monitor")`
3. Read `configuration.rssUrl()` and construct `RssFeedItemRepository`
4. Build strategy list from boolean config flags
5. `EqualsOrBelowPriceMonitorStrategy` is only added when flag is `true` and `priceThreshold()` is non-null
6. Create `MonitorCommand` and delegate to `commandExecutionScript.execute()`

## Manifest

```json
{
  "package_name": "com.cereal-automation.monitor.rss",
  "name": "RSS Monitor",
  "version_code": 1
}
```
