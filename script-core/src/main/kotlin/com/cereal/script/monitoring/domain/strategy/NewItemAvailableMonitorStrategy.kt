package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.models.requireValue
import java.time.Instant

class NewItemAvailableMonitorStrategy(
    private val since: Instant,
) : MonitorStrategy {
    override suspend fun shouldNotify(item: Item): Boolean =
        item.requireValue<ItemValue.PublishDate>().value?.let {
            it > since
        } ?: false

    override fun getNotificationMessage(item: Item): String = "Found new item: ${item.name}."
}
