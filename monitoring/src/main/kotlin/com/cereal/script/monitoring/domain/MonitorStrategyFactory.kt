package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.script.monitoring.domain.strategy.EqualsOrBelowPriceMonitorStrategy
import com.cereal.script.monitoring.domain.strategy.MonitorStrategy
import com.cereal.script.monitoring.domain.strategy.NewItemAvailableMonitorStrategy
import com.cereal.script.monitoring.domain.strategy.PriceDropMonitorStrategy
import com.cereal.script.monitoring.domain.strategy.StockAvailableMonitorStrategy

class MonitorStrategyFactory {
    fun create(mode: MonitorMode): MonitorStrategy =
        when (mode) {
            is MonitorMode.NewItemAvailable -> NewItemAvailableMonitorStrategy(mode.since)
            is MonitorMode.EqualsOrBelowPrice -> EqualsOrBelowPriceMonitorStrategy(mode.price, mode.currency)
            is MonitorMode.StockAvailable -> StockAvailableMonitorStrategy()
            is MonitorMode.PriceDrop -> PriceDropMonitorStrategy()
        }
}
