package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.getValue
import com.cereal.command.monitor.models.requireValue

/**
 * A monitoring strategy to notify only on availability increases (restocks and new in-stock variants).
 *
 * Use this when you only want positive signals and to avoid noise from stock decreases or out-of-stock events.
 * - Notifies when an item transitions from not-in-stock to in-stock.
 * - Notifies when a new variant appears in stock.
 * - Notifies when an existing variant transitions from out-of-stock to in-stock.
 *
 * Requires a baseline so that the first scraping cycle does not notify spuriously.
 */
class StockAvailableMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        val availableStockMessage = generateAvailableStockMessage(item, previousItem)
        if (availableStockMessage != null) return availableStockMessage

        return generateVariantChangesMessage(item, previousItem)
    }

    private fun generateAvailableStockMessage(
        item: Item,
        previousItem: Item?,
    ): String? {
        val currentStock = item.getValue<ItemProperty.Stock>()
        val previousStock = previousItem?.getValue<ItemProperty.Stock>()

        return if (currentStock?.isInStock == true && previousStock?.isInStock != true) {
            "${item.name} is in stock (${currentStock.stockValue()})!"
        } else {
            null
        }
    }

    /**
     * Generates a notification message describing changes in item variants, such as new variants being
     * added to stock or previously out-of-stock variants being restocked.
     *
     * @param item The current item state to evaluate.
     * @param previousItem The previous item state, or null if unavailable (e.g., first-time evaluation).
     * @return A string message detailing the changes in variants if any, or null if no changes are detected.
     */
    private fun generateVariantChangesMessage(
        item: Item,
        previousItem: Item?,
    ): String? {
        val currentVariants = item.variants
        val previousVariants = previousItem?.variants

        val newVariantsMessages =
            currentVariants
                .filter {
                    it.requireValue<ItemProperty.Stock>().isInStock &&
                        previousVariants?.none { prev ->
                            prev.id == it.id
                        } ?: true
                }.map { variant ->
                    val stockLevelString = variant.getValue<ItemProperty.Stock>()?.stockValue()
                    listOfNotNull("New variant ${variant.name} is in stock", stockLevelString).joinToString(": ")
                }

        val restockedVariantsMessages =
            currentVariants
                .filter {
                    it.requireValue<ItemProperty.Stock>().isInStock &&
                        previousVariants?.any { prev -> prev.id == it.id && !prev.requireValue<ItemProperty.Stock>().isInStock }
                            ?: false
                }.map { variant ->
                    val stockLevelString =
                        variant.getValue<ItemProperty.Stock>()?.let {
                            "${it.stockValue()}"
                        }
                    listOfNotNull("Variant ${variant.name} is in stock", stockLevelString).joinToString(": ")
                }

        val combinedMessages = newVariantsMessages + restockedVariantsMessages
        return if (combinedMessages.isNotEmpty()) combinedMessages.joinToString("\n") else null
    }

    override fun requiresBaseline(): Boolean = true
}
