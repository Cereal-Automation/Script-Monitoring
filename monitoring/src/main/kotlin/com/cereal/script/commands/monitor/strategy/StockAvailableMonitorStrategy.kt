package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import com.cereal.script.commands.monitor.domain.models.getValue

class StockAvailableMonitorStrategy : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        if (previousItem == null) return null

        val availableStock = item.getValue<ItemProperty.AvailableStock>()?.value
        val availableStockPrevious = previousItem.getValue<ItemProperty.AvailableStock>()?.value
        if (availableStock != null && availableStockPrevious != null) {
            if (availableStockPrevious == 0 && availableStock > 0) {
                return "${item.name} is in stock ($availableStock)!"
            }
        }

        val variants = item.getValue<ItemProperty.Variants>()?.value
        val variantsPrevious = previousItem.getValue<ItemProperty.Variants>()?.value
        if (variants != null && variantsPrevious != null) {
            val newVariantsWithStock =
                variants
                    .filter { variant ->
                        variant.inStock && variantsPrevious.none { it.name == variant.name }
                    }.map {
                        "New variant ${it.name} is in stock: ${it.stockLevel}"
                    }
            val variantsThatBecameInStock =
                variants
                    .filter { variant ->
                        variant.inStock &&
                            variantsPrevious.any { previousVariant -> previousVariant.name == variant.name && !previousVariant.inStock }
                    }.map {
                        "Variant ${it.name} is in stock: ${it.stockLevel}"
                    }

            val messages = newVariantsWithStock + variantsThatBecameInStock
            if (messages.isNotEmpty()) {
                return messages.joinToString("\n")
            }
        }

        return null
    }

    override fun requiresBaseline(): Boolean = true
}
