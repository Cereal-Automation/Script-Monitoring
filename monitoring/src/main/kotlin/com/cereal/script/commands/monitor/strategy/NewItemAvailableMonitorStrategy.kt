package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemValue
import com.cereal.script.commands.monitor.domain.models.getValue
import java.time.Instant

class NewItemAvailableMonitorStrategy(
    private val since: Instant = Instant.now(),
) : MonitorStrategy {
    private val detectedItems: MutableSet<String> = mutableSetOf()

    override suspend fun shouldNotify(
        item: Item,
        runSequenceNumber: Int,
    ): Boolean =
        item.getValue<ItemValue.PublishDate>()?.value?.let {
            it > since
        } ?: isNewItemDetected(item, runSequenceNumber)

    override fun getNotificationMessage(item: Item): String = "Found new item: ${item.name}."

    private fun isNewItemDetected(
        item: Item,
        runSequenceNumber: Int,
    ): Boolean {
        if (runSequenceNumber == 1) {
            // First run so have to build up the map.
            detectedItems.add(item.id)
            return false
        } else {
            return detectedItems.add(item.id)
        }
    }
}
