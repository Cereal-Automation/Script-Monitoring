package com.cereal.script.monitoring.domain.models

import java.math.BigDecimal

sealed class Value {
    data class Price(val value: BigDecimal): Value()
}

data class Item(val id: String, val url: String, val name: String, val values: List<Value>? = null)
