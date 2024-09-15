package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.models.requireValue
import java.time.Instant

class NewItemMonitorStrategy(private val since: Instant): MonitorStrategy {

    override suspend fun shouldNotify(item: Item): Boolean {
        val publishDate = item.requireValue<ItemValue.PublishDate>()

        return publishDate.publishDate > since
    }

    override fun getNotificationMessage(item: Item): String {
        return "Found new item: ${item.name}."
    }
}
