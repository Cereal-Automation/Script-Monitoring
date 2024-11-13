package com.cereal.script.monitoring.domain.models

import java.math.BigDecimal
import java.time.Instant

sealed class MonitorMode {
    data class NewItemAvailable(
        val since: Instant,
    ) : MonitorMode()

    data class EqualsOrBelowPrice(
        val price: BigDecimal,
        val currency: Currency,
    ) : MonitorMode()

    data object PriceDrop : MonitorMode()

    data object StockAvailable : MonitorMode()
}
