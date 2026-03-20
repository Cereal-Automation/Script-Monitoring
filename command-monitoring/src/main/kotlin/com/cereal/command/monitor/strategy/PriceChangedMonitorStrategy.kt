package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.getValue

/**
 * Monitoring strategy that notifies when the price of an in-stock item changes in either direction.
 *
 * Designed for Dynamic Price scenarios (e.g., TGTG Belgium) where a shop may adjust bag prices
 * while stock is available. Notifies on both price increases and decreases.
 *
 * Requirements:
 * - Requires a baseline (previous item state); silent on first poll cycle.
 * - Only fires when the item is currently in stock.
 * - Suppressed when price or stock data is missing.
 */
class PriceChangedMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        if (previousItem == null) return null

        val currentPrice = item.getValue<ItemProperty.Price>() ?: return null
        val previousPrice = previousItem.getValue<ItemProperty.Price>() ?: return null

        if (currentPrice.currency != previousPrice.currency) return null

        val stock = item.getValue<ItemProperty.Stock>() ?: return null
        if (!stock.isInStock) return null

        if (currentPrice.value.compareTo(previousPrice.value) == 0) return null

        val direction = if (currentPrice.value < previousPrice.value) "↓" else "↑"
        val currency = currentPrice.currency.code

        return "Price for ${item.name} changed: ${previousPrice.value} $currency → ${currentPrice.value} $currency ($direction)"
    }

    override fun requiresBaseline(): Boolean = true
}
