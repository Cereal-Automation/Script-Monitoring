package com.cereal.rss

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem

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
    fun priceThreshold(): String?

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
