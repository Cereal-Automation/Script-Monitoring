package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.models.getValue
import com.cereal.script.monitoring.domain.models.requireValue
import java.math.BigDecimal

class PriceDropMonitorStrategy : MonitorStrategy {
    private val itemToPrice: HashMap<String, BigDecimal> = HashMap()

    override suspend fun shouldNotify(item: Item): Boolean {
        val price = item.getValue<ItemValue.Price>()?.value ?: return false

        val hasPriceDropped =
            itemToPrice[item.id]?.let {
                it.compareTo(price) == 1
            } ?: false

        itemToPrice[item.id] = price
        return hasPriceDropped
    }

    override fun getNotificationMessage(item: Item): String {
        val price = item.requireValue<ItemValue.Price>()

        return "Price for ${item.name} dropped to $price."
    }
}
