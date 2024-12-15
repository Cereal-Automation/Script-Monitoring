package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.getValue
import java.time.Instant

class NewItemAvailableMonitorStrategy(
    private val since: Instant = Instant.now(),
) : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): Boolean =
        item.getValue<ItemProperty.PublishDate>()?.value?.let {
            it > since
        } ?: (previousItem == null)

    override fun requiresBaseline(): Boolean = true

    override fun getNotificationMessage(item: Item): String = "Found new item: ${item.name}."
}
