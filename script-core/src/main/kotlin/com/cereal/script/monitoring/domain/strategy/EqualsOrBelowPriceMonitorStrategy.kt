package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.models.requireValue
import java.math.BigDecimal


class EqualsOrBelowPriceMonitorStrategy(private val price: BigDecimal): MonitorStrategy {

    override suspend fun shouldNotify(item: Item): Boolean {
        val itemPrice = item.requireValue<ItemValue.Price>().price
        return itemPrice <= price
    }

    override fun getNotificationMessage(item: Item): String {
        val itemPrice = item.requireValue<ItemValue.Price>().price
        return "${item.name} is available for $itemPrice"
    }
}
