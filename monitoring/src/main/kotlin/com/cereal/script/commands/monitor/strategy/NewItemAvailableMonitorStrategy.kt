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
