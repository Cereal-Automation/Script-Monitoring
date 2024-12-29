package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.getValue

/**
 * A monitoring strategy to notify when an item becomes available in stock or when there are changes to its variants.
 *
 * This strategy checks the stock status and variant details of the current item against its previous state:
 * - If the item had no stock previously and now has stock, a notification is triggered.
 * - If the item's variants have new additions that are in stock, or previously out-of-stock variants are restocked,
 *   a corresponding message is generated.
 *
 * Implements the [MonitorStrategy] interface, defining the logic for generating notifications and indicating
 * a requirement for a baseline (a previous state) to function properly.
 */
class StockAvailableMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        val availableStockMessage = generateAvailableStockMessage(item, previousItem)
        if (availableStockMessage != null) return availableStockMessage

        val variantChangesMessage = generateVariantChangesMessage(item, previousItem)
        return variantChangesMessage
    }

    private fun generateAvailableStockMessage(
        item: Item,
        previousItem: Item?,
    ): String? {
        val currentStock = item.getValue<ItemProperty.AvailableStock>()?.value
        val previousStock = previousItem?.getValue<ItemProperty.AvailableStock>()?.value
        val hadNoStock = (previousStock == null || previousStock == 0)
        return if (currentStock != null && hadNoStock && currentStock > 0) {
            "${item.name} is in stock ($currentStock)!"
        } else {
            null
        }
    }

    private fun generateVariantChangesMessage(
        item: Item,
        previousItem: Item?,
    ): String? {
        val currentVariants = item.getValue<ItemProperty.Variants>()?.value
        val previousVariants = previousItem?.getValue<ItemProperty.Variants>()?.value
        if (currentVariants == null) return null

        val newVariantsMessages =
            currentVariants
                .filter { it.inStock && previousVariants?.none { prev -> prev.name == it.name } ?: true }
                .map { "New variant ${it.name} is in stock: ${it.stockLevel}" }

        val restockedVariantsMessages =
            currentVariants
                .filter { it.inStock && previousVariants?.any { prev -> prev.name == it.name && !prev.inStock } ?: false }
                .map { "Variant ${it.name} is in stock: ${it.stockLevel}" }

        val combinedMessages = newVariantsMessages + restockedVariantsMessages
        return if (combinedMessages.isNotEmpty()) combinedMessages.joinToString("\n") else null
    }

    override fun requiresBaseline(): Boolean = true
}