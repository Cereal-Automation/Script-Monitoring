package com.cereal.script.monitoring.domain.models

sealed class MonitorMode(val value: Value? = null) {
    data object NewItem: MonitorMode()
    data class EqualsOrBelowPrice(val price: Value.Price): MonitorMode(price)
}
