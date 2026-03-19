package com.cereal.rss

import com.cereal.command.monitor.data.rss.RssFeedItemRepository
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.strategy.MonitorStrategy

class FilteredNewItemMonitorStrategy(
    private val baselineStrategy: MonitorStrategy,
    private val keywords: List<String>,
    private val authors: List<String>,
    private val categories: List<String>,
) : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        // Delegate baseline newness check
        val baselineMessage = baselineStrategy.shouldNotify(item, previousItem) ?: return null

        // If no filters configured, behave identically to original
        if (keywords.isEmpty() && authors.isEmpty() && categories.isEmpty()) {
            return baselineMessage
        }

        val matchesKeyword =
            keywords.isNotEmpty() &&
                keywords.any { keyword ->
                    item.name.contains(keyword, ignoreCase = true) ||
                        (item.description?.contains(keyword, ignoreCase = true) == true)
                }

        val itemAuthors =
            item.properties.filterIsInstance<ItemProperty.Custom>().filter {
                it.name == RssFeedItemRepository.PROPERTY_AUTHOR
            }.map { it.value }
        val matchesAuthor =
            authors.isNotEmpty() &&
                authors.any { configured ->
                    itemAuthors.any { it.equals(configured, ignoreCase = true) }
                }

        val itemCategories =
            item.properties.filterIsInstance<ItemProperty.Custom>().filter {
                it.name == RssFeedItemRepository.PROPERTY_CATEGORY
            }.map { it.value }
        val matchesCategory =
            categories.isNotEmpty() &&
                categories.any { configuredCategory ->
                    itemCategories.any { it.equals(configuredCategory, ignoreCase = true) }
                }

        val passed = matchesKeyword || matchesAuthor || matchesCategory

        return if (passed) baselineMessage else null
    }

    override fun requiresBaseline(): Boolean = baselineStrategy.requiresBaseline()
}
