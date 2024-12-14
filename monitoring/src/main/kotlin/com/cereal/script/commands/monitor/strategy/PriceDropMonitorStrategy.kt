package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.getValue
import com.cereal.script.commands.monitor.domain.models.requireValue
import java.math.BigDecimal

class PriceDropMonitorStrategy : MonitorStrategy {
    private val itemToPrice: HashMap<String, BigDecimal> = HashMap()

    override suspend fun shouldNotify(
        item: Item,
        runSequenceNumber: Int,
    ): Boolean {
        val price = item.getValue<ItemProperty.Price>()?.value ?: return false

        val hasPriceDropped =
            itemToPrice[item.id]?.let {
                it.compareTo(price) == 1
            } ?: false

        itemToPrice[item.id] = price
        return hasPriceDropped
    }

    override fun getNotificationMessage(item: Item): String {
        val price = item.requireValue<ItemProperty.Price>()

        return "Price for ${item.name} dropped to $price."
    }
}
