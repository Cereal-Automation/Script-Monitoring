package com.cereal.script.monitoring.domain.models

import java.math.BigDecimal
import java.time.Instant

sealed class MonitorMode {
    data class NewItem(val since: Instant): MonitorMode()
    data class EqualsOrBelowPrice(val price: BigDecimal): MonitorMode()
}
