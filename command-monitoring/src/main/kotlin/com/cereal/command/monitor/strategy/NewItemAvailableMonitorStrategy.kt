package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.getValue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
 * The [requiresBaseline] behaviour can be controlled via the `requiresBaseline` parameter. When `false`,
 * the strategy will notify for every item it has not seen before, even on the very first run.
 */
@OptIn(ExperimentalTime::class)
class NewItemAvailableMonitorStrategy(
    private val since: Instant = Clock.System.now(),
    private val baseline: Boolean = true,
) : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): MonitorStrategy.NotifyResult {
        val isNewItem =
            item.getValue<ItemProperty.PublishDate>()?.value?.let {
                it > since
            } ?: (previousItem == null)

        return if (isNewItem) {
            MonitorStrategy.NotifyResult.Notify("Found new item: ${item.name}.")
        } else {
            MonitorStrategy.NotifyResult.Skip("Not a new item")
        }
    }

    override fun requiresBaseline(): Boolean = baseline
}
