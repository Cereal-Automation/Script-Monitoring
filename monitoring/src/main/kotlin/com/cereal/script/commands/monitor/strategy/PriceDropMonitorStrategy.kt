package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.getValue
import com.cereal.script.commands.monitor.domain.models.requireValue

class PriceDropMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): Boolean {
        if (previousItem == null) return false

        val price = item.getValue<ItemProperty.Price>()?.value ?: return false
        val previousPrice = previousItem.getValue<ItemProperty.Price>()?.value ?: return false

        return previousPrice.compareTo(price) == 1
    }

    override fun requiresBaseline(): Boolean = true

    override fun getNotificationMessage(item: Item): String {
        val price = item.requireValue<ItemProperty.Price>()

        return "Price for ${item.name} dropped to $price."
    }
}
