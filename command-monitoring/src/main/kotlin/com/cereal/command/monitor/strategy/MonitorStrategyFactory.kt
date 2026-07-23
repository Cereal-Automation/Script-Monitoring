package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Currency
import java.math.BigDecimal
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object MonitorStrategyFactory {
    fun priceDropMonitorStrategy(): MonitorStrategy = PriceDropMonitorStrategy()

    fun stockAvailableMonitorStrategy(): MonitorStrategy = StockAvailableMonitorStrategy()

    fun stockChangedMonitorStrategy(): MonitorStrategy = StockChangedMonitorStrategy()

    fun newItemAvailableMonitorStrategy(
        since: Instant,
        requiresBaseline: Boolean = true,
    ): MonitorStrategy = NewItemAvailableMonitorStrategy(since, requiresBaseline)

    fun equalsOrBelowPriceMonitorStrategy(
        price: BigDecimal,
        currency: Currency,
    ): MonitorStrategy = EqualsOrBelowPriceMonitorStrategy(price, currency)
}
