package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.getValue

/**
 * Monitoring strategy that notifies when the price of an item decreases compared to its previous state.
 *
 * This strategy implements the [MonitorStrategy] interface to track price drops for a given item by:
 * - Comparing the current and previous prices of the item.
 * - Generating a notification message if the current price is lower than the previous price.
 *
 * Provides a [requiresBaseline] method to indicate that a baseline (previously observed item state) is required
 * for the accurate functioning of the strategy.
 */
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
