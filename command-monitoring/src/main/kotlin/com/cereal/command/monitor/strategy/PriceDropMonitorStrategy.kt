package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.getValue

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
    ): MonitorStrategy.NotifyResult {
        if (previousItem == null) return MonitorStrategy.NotifyResult.Skip("No previous item")

        val price = item.getValue<ItemProperty.Price>()?.value ?: return MonitorStrategy.NotifyResult.Skip("No price")
        val previousPrice =
            previousItem.getValue<ItemProperty.Price>()?.value
                ?: return MonitorStrategy.NotifyResult.Skip("No previous price")

        return if (previousPrice.compareTo(price) == 1) {
            MonitorStrategy.NotifyResult.Notify("Price for ${item.name} dropped to $price.")
        } else {
            MonitorStrategy.NotifyResult.Skip("Price did not drop")
        }
    }

    override fun requiresBaseline(): Boolean = true
}
