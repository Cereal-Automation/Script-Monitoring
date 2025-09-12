package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.repository.MarketItemRepository

class MarketPriceComparisonStrategy(
    private val marketItemRepository: MarketItemRepository,
) : MonitorStrategy {
    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String? {
        // TODO: Implement market price comparison logic
        // TODO: Returning null for now (no notification)
        return null
    }

    override fun requiresBaseline(): Boolean = false
}
