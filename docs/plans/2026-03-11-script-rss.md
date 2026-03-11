# RSS Monitor Script Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Create a new `script-rss` Gradle module that lets users supply an RSS feed URL and toggle all five monitoring strategies via configuration.

**Architecture:** Follows the identical structure of `script-nike`: a `RssConfiguration` interface extending `BaseConfiguration`, a `RssScript` class implementing `Script<RssConfiguration>`, and a `StateModifier` for the conditional price-threshold field. Reuses the existing `RssFeedItemRepository` and `MonitorStrategyFactory` from `command-monitoring`.

**Tech Stack:** Kotlin JVM 17, Gradle Kotlin DSL, Cereal SDK (compileOnly), `command-monitoring`, `script-common`, `command` modules.

---

## Task 1: Register the module in settings and create the build file

**Files:**
- Modify: `settings.gradle.kts` (root)
- Create: `script-rss/build.gradle.kts`

**Step 1: Add `:script-rss` to settings.gradle.kts**

Open `settings.gradle.kts` and add `include("script-rss")` after the last `include(...)` line (currently `include("script-zalando")`).

```kotlin
include("script-rss")
```

**Step 2: Create `script-rss/build.gradle.kts`**

Copy this exactly — it is identical to `script-sample/build.gradle.kts` and `script-nike/build.gradle.kts`:

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

**Step 3: Create the source directory structure**

```bash
mkdir -p script-rss/src/main/kotlin/com/cereal/rss
mkdir -p script-rss/src/main/resources
mkdir -p script-rss/src/test/kotlin/com/cereal/rss
```

**Step 4: Verify Gradle can resolve the module**

```bash
./gradlew :script-rss:dependencies
```

Expected: dependency tree printed without errors.

**Step 5: Commit**

```bash
git add settings.gradle.kts script-rss/
git commit -m "feat: scaffold script-rss module"
```

---

## Task 2: Create the manifest

**Files:**
- Create: `script-rss/src/main/resources/manifest.json`

**Step 1: Write the manifest**

```json
{
  "package_name": "com.cereal-automation.monitor.rss",
  "name": "RSS Monitor",
  "version_code": 1
}
```

**Step 2: Commit**

```bash
git add script-rss/src/main/resources/manifest.json
git commit -m "feat: add script-rss manifest"
```

---

## Task 3: Create the StateModifier for the price threshold field

The `price_threshold` config field should only be visible when `monitor_price_threshold` is `true`. A `StateModifier` handles this.

**Files:**
- Create: `script-rss/src/main/kotlin/com/cereal/rss/RssStateModifiers.kt`

**Step 1: Write the StateModifier**

```kotlin
package com.cereal.rss

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

object PriceThresholdStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? = null

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility {
        val monitorPriceThreshold = scriptConfig.valueForKey(RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD)
        return if (monitorPriceThreshold is ScriptConfigValue.BooleanScriptConfigValue && monitorPriceThreshold.value) {
            Visibility.VisibleRequired
        } else {
            Visibility.Hidden
        }
    }
}
```

> Note: `Visibility.Hidden` hides the field entirely. `Visibility.VisibleRequired` makes it required when visible. Check that `ScriptConfigValue.BooleanScriptConfigValue` exists — if the SDK uses a different name for boolean config values, look at how `TgtgStateModifiers.kt` reads other value types and adjust accordingly. Look at the existing `TgtgStateModifiers.kt` for reference on the SDK's `ScriptConfigValue` subtypes.

**Step 2: Commit**

```bash
git add script-rss/src/main/kotlin/com/cereal/rss/RssStateModifiers.kt
git commit -m "feat: add PriceThresholdStateModifier for RSS script"
```

---

## Task 4: Create `RssConfiguration`

**Files:**
- Create: `script-rss/src/main/kotlin/com/cereal/rss/RssConfiguration.kt`

**Step 1: Write the configuration interface**

```kotlin
package com.cereal.rss

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import java.math.BigDecimal

interface RssConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_RSS_URL,
        name = "RSS Feed URL",
        description = "The URL of the RSS feed to monitor.",
        isScriptIdentifier = true,
    )
    fun rssUrl(): String

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_NEW_ITEMS,
        name = "Monitor new items",
        description = "If enabled, send a notification when a new item appears in the feed.",
    )
    fun monitorNewItems(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_PRICE_DROP,
        name = "Monitor price drops",
        description = "If enabled, send a notification when the price of an item drops.",
    )
    fun monitorPriceDrop(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_STOCK_AVAILABLE,
        name = "Monitor stock available",
        description = "If enabled, send a notification when an item comes back in stock.",
    )
    fun monitorStockAvailable(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_STOCK_CHANGED,
        name = "Monitor stock changes",
        description = "If enabled, send a notification when the stock status of an item changes.",
    )
    fun monitorStockChanged(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_PRICE_THRESHOLD,
        name = "Monitor price threshold",
        description = "If enabled, send a notification when an item's price is at or below the configured threshold.",
    )
    fun monitorPriceThreshold(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_PRICE_THRESHOLD,
        name = "Price threshold",
        description = "The price threshold to monitor. Notifications are sent when the price is at or below this value.",
        stateModifier = PriceThresholdStateModifier::class,
    )
    fun priceThreshold(): BigDecimal?

    companion object {
        const val KEY_RSS_URL = "rss_url"
        const val KEY_MONITOR_NEW_ITEMS = "monitor_new_items"
        const val KEY_MONITOR_PRICE_DROP = "monitor_price_drop"
        const val KEY_MONITOR_STOCK_AVAILABLE = "monitor_stock_available"
        const val KEY_MONITOR_STOCK_CHANGED = "monitor_stock_changed"
        const val KEY_MONITOR_PRICE_THRESHOLD = "monitor_price_threshold"
        const val KEY_PRICE_THRESHOLD = "price_threshold"
    }
}
```

**Step 2: Verify the module compiles**

```bash
./gradlew :script-rss:compileKotlin
```

Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add script-rss/src/main/kotlin/com/cereal/rss/RssConfiguration.kt
git commit -m "feat: add RssConfiguration interface"
```

---

## Task 5: Create `RssScript`

**Files:**
- Create: `script-rss/src/main/kotlin/com/cereal/rss/RssScript.kt`

**Step 1: Write the script**

```kotlin
package com.cereal.rss

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.rss.RssFeedItemRepository
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.command.monitor.strategy.MonitorStrategyFactory
import com.cereal.command.monitor.models.Currency
import com.cereal.script.CommandExecutionScript
import com.cereal.script.commands.Command
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RssScript : Script<RssConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.rss",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: RssConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: RssConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: RssConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }

    private fun buildCommands(
        configuration: RssConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("RSS Monitor")
        val itemRepository = RssFeedItemRepository(
            rssFeedUrl = configuration.rssUrl(),
            logger = provider.logger(),
        )

        val strategies = buildMonitorStrategies(configuration)

        return listOf(
            factory.monitorCommand(
                itemRepository = itemRepository,
                logRepository = logRepository,
                notificationRepository = notificationRepository,
                strategies = strategies,
                scrapeInterval = configuration.monitorInterval()?.seconds,
            ),
        )
    }

    private fun buildMonitorStrategies(configuration: RssConfiguration): List<MonitorStrategy> =
        buildList {
            if (configuration.monitorNewItems()) {
                add(MonitorStrategyFactory.newItemAvailableMonitorStrategy(Clock.System.now()))
            }
            if (configuration.monitorPriceDrop()) {
                add(MonitorStrategyFactory.priceDropMonitorStrategy())
            }
            if (configuration.monitorStockAvailable()) {
                add(MonitorStrategyFactory.stockAvailableMonitorStrategy())
            }
            if (configuration.monitorStockChanged()) {
                add(MonitorStrategyFactory.stockChangedMonitorStrategy())
            }
            val threshold = configuration.priceThreshold()
            if (configuration.monitorPriceThreshold() && threshold != null) {
                add(MonitorStrategyFactory.equalsOrBelowPriceMonitorStrategy(threshold, Currency.EUR))
            }
        }
}
```

> **Important notes:**
> - Check `MonitorStrategyFactory` for the exact method names — look at `command-monitoring/src/main/kotlin/com/cereal/command/monitor/strategy/MonitorStrategyFactory.kt` to verify `stockAvailableMonitorStrategy()`, `stockChangedMonitorStrategy()`, and `equalsOrBelowPriceMonitorStrategy(price, currency)` exist with those signatures.
> - `Currency.EUR` is used as a placeholder. The `EqualsOrBelowPriceMonitorStrategy` requires a currency. If a `currency` config field is needed, add it to `RssConfiguration`. For now EUR is the default — adjust if the `Currency` type in `command-monitoring` has a different API.
> - The `provider.logger()` call is how `NikeItemRepository` and `RssFeedItemRepository` receive the logger — verify this is the correct method name on `ComponentProvider`.

**Step 2: Verify the module compiles**

```bash
./gradlew :script-rss:compileKotlin
```

Expected: BUILD SUCCESSFUL. Fix any import or method-name issues by checking `MonitorStrategyFactory.kt` and `ComponentProvider`.

**Step 3: Commit**

```bash
git add script-rss/src/main/kotlin/com/cereal/rss/RssScript.kt
git commit -m "feat: implement RssScript with configurable strategies"
```

---

## Task 6: Verify the full build

**Step 1: Run the full build**

```bash
./gradlew :script-rss:build
```

Expected: BUILD SUCCESSFUL, no compilation errors, no test failures.

**Step 2: Confirm the shadow JAR is producible**

```bash
./gradlew :script-rss:shadowJar
```

Expected: JAR produced in `script-rss/build/libs/`.

**Step 3: Commit any fixes, then final commit**

```bash
git add -A
git commit -m "feat: complete script-rss implementation"
```
