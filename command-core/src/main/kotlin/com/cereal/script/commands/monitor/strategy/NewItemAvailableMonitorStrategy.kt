package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.getValue
import java.time.Instant

/**
 * A monitoring strategy that notifies when a new item is detected since a specific point in time.
 *
 * This class implements the [MonitorStrategy] interface to identify newly published items.
 * It checks the `PublishDate` property of the given item and compares it to the specified reference time.
 * If the `PublishDate` of the item is more recent than the reference time, or if there is no previous item
 * , a notification will be triggered.
 *
 * The reference point can be customized through the `since` parameter or defaults to the current time.
 *
 * Provides a [requiresBaseline] method which indicates that the strategy requires an initial baseline
 * or previous item for consistent notification behavior.
 */
class NewItemAvailableMonitorStrategy(
    private val since: Instant = Instant.now(),
) : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        val isNewItem =
            item.getValue<ItemProperty.PublishDate>()?.value?.let {
                it > since
            } ?: (previousItem == null)

        return if (isNewItem) {
            "Found new item: ${item.name}."
        } else {
            null
        }
    }

    override fun requiresBaseline(): Boolean = true
}
