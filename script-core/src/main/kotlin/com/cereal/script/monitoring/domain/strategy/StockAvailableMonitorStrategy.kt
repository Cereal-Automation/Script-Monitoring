package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.models.requireValue

class StockAvailableMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(item: Item): Boolean {
        val availableStock = item.requireValue<ItemValue.AvailableStock>().value
        return availableStock > 0
    }

    override fun getNotificationMessage(item: Item): String {
        val availableStock = item.requireValue<ItemValue.AvailableStock>().value

        return "${item.name} is in stock ($availableStock)!"
    }
}
