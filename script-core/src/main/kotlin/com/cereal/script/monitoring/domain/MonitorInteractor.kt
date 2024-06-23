package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.script.monitoring.domain.repository.ItemMonitorRepository
import com.cereal.script.monitoring.domain.strategy.EqualsOrBelowPriceMonitorStrategy
import com.cereal.script.monitoring.domain.strategy.NewItemMonitorStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MonitorInteractor(private val itemMonitorRepository: ItemMonitorRepository) {

    private val monitorStrategyMap = mapOf(
        MonitorMode.NewItem::class to NewItemMonitorStrategy(),
        MonitorMode.EqualsOrBelowPrice::class to EqualsOrBelowPriceMonitorStrategy()
    )

    suspend operator fun invoke(config: Config): Flow<MonitoredItem> {
        val strategy = monitorStrategyMap[config.mode::class] ?: throw RuntimeException("No strategy found for monitor mode ${config.mode}")

        return itemMonitorRepository.getItems().map { item ->
            val notify = strategy.shouldNotify(item)
            MonitoredItem(item, notify)
        }
    }

    data class Config(val mode: MonitorMode)

    data class MonitoredItem(
        val item: Item,
        val notify: Boolean,
    )

}
