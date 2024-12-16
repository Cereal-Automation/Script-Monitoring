package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.getValue

class PriceDropMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        if (previousItem == null) return null

        val price = item.getValue<ItemProperty.Price>()?.value ?: return null
        val previousPrice = previousItem.getValue<ItemProperty.Price>()?.value ?: return null

        return if (previousPrice.compareTo(price) == 1) {
            "Price for ${item.name} dropped to $price."
        } else {
            null
        }
    }

    override fun requiresBaseline(): Boolean = true
}
