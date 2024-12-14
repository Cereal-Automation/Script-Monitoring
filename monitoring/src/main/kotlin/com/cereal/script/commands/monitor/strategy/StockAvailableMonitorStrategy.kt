package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.requireValue

class StockAvailableMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        runSequenceNumber: Int,
    ): Boolean {
        val availableStock = item.requireValue<ItemProperty.AvailableStock>().value
        return availableStock > 0
    }

    override fun getNotificationMessage(item: Item): String {
        val availableStock = item.requireValue<ItemProperty.AvailableStock>().value

        return "${item.name} is in stock ($availableStock)!"
    }
}
