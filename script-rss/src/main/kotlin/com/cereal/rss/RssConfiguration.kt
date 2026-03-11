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

    companion object {
        const val KEY_RSS_URL = "rss_url"
        const val KEY_MONITOR_NEW_ITEMS = "monitor_new_items"
    }
}
