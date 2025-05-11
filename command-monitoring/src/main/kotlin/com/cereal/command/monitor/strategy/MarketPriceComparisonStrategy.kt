package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.repository.MarketItemRepository

class MarketPriceComparisonStrategy(private val marketItemRepository: MarketItemRepository) : MonitorStrategy {

    override suspend fun shouldNotify(
        item: Item,
        previousItem: Item?
    ): String? {
        throw NotImplementedError()
    }

    override fun requiresBaseline(): Boolean = false
}