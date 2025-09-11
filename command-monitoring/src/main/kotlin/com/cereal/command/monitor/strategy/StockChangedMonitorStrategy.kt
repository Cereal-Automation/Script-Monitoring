package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.getValue
import com.cereal.command.monitor.models.requireValue

/**
 * A monitoring strategy to notify on any stock state change at item- or variant-level.
 *
 * Use this when you care about both directions (restock and out-of-stock) and stock level deltas.
 * - Notifies when an item toggles in/out of stock.
 * - Notifies when stock level value changes (amount/level string).
 * - Aggregates variant-level changes similarly.
 *
 * Does not require a baseline for initial scraping cycle because it can compare nullable previous values
 * and still remain silent if no difference is determinable.
 */
class StockChangedMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        val stockMessage = generateStockChangeMessage(item, previousItem)
        if (stockMessage != null) return stockMessage

        return generateVariantChangesMessage(item, previousItem)
    }

    private fun generateStockChangeMessage(
        item: Item,
        previousItem: Item?,
    ): String? {
        val currentStock = item.getValue<ItemProperty.Stock>()
        val previousStock = previousItem?.getValue<ItemProperty.Stock>()

        if (currentStock == null) return null

        return when {
            currentStock.isInStock != previousStock?.isInStock -> {
                if (currentStock.isInStock) {
                    "${item.name} is now in stock (${currentStock.stockValue()})!"
                } else {
                    "${item.name} is now out of stock"
                }
            }
            currentStock.stockValue() != previousStock.stockValue() -> {
                "${item.name} stock level changed: ${previousStock.stockValue()} → ${currentStock.stockValue()}"
            }
            else -> null
        }
    }

    private fun generateVariantChangesMessage(
        item: Item,
        previousItem: Item?,
    ): String? {
        val currentVariants = item.variants
        val previousVariants = previousItem?.variants ?: emptyList()

        val changedVariantsMessages =
            currentVariants.mapNotNull { variant ->
                val currentStock = variant.requireValue<ItemProperty.Stock>()
                val previousStock =
                    previousVariants
                        .find { it.id == variant.id }
                        ?.requireValue<ItemProperty.Stock>()

                when {
                    currentStock.isInStock != previousStock?.isInStock -> {
                        if (currentStock.isInStock) {
                            "Variant ${variant.name} is now in stock (${currentStock.stockValue()})"
                        } else {
                            "Variant ${variant.name} is now out of stock"
                        }
                    }
                    currentStock.stockValue() != previousStock.stockValue() -> {
                        "Variant ${variant.name} stock level changed: ${previousStock.stockValue()} → ${currentStock.stockValue()}"
                    }
                    else -> null
                }
            }

        return changedVariantsMessages.takeIf { it.isNotEmpty() }?.joinToString("\n")
    }

    override fun requiresBaseline(): Boolean = false
}
