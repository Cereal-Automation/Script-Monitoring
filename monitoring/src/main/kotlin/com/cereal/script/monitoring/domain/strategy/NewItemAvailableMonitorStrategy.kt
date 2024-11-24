package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.models.getValue
import java.time.Instant

class NewItemAvailableMonitorStrategy(
    private val since: Instant = Instant.now(),
) : MonitorStrategy {
    private val detectedItems: MutableSet<String> = mutableSetOf()

    override suspend fun shouldNotify(
        item: Item,
        execution: Execution,
    ): Boolean =
        item.getValue<ItemValue.PublishDate>()?.value?.let {
            it > since
        } ?: isNewItemDetected(item, execution)

    override fun getNotificationMessage(item: Item): String = "Found new item: ${item.name}."

    private fun isNewItemDetected(
        item: Item,
        execution: Execution,
    ): Boolean {
        if (execution.sequenceNumber == 1) {
            // First run so have to build up the map.
            detectedItems.add(item.id)
            return false
        } else {
            return detectedItems.add(item.id)
        }
    }
}
