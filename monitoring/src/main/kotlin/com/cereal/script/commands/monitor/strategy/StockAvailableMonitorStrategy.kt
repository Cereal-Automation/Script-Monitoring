package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.requireValue

class StockAvailableMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): Boolean {
        if (previousItem == null) return false

        val availableStock = item.requireValue<ItemProperty.AvailableStock>().value
        val availableStockPrevious = previousItem.requireValue<ItemProperty.AvailableStock>().value
        return availableStockPrevious == 0 && availableStock > 0
    }

    override fun requiresBaseline(): Boolean = true

    override fun getNotificationMessage(item: Item): String {
        val availableStock = item.requireValue<ItemProperty.AvailableStock>().value

        return "${item.name} is in stock ($availableStock)!"
    }
}
