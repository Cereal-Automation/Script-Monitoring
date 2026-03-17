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
        keyName = KEY_FILTER_KEYWORDS,
        name = "Keywords",
        description = "Comma-separated list of keywords to search for in title or description.",
    )
    fun filterKeywords(): String?

    @ScriptConfigurationItem(
        keyName = KEY_FILTER_AUTHORS,
        name = "Authors",
        description = "Comma-separated list of author names to match (case-insensitive).",
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
}
