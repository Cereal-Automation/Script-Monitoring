package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.getValue

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
