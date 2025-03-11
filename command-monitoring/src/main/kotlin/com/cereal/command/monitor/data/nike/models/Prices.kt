package com.cereal.command.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Prices(
    @SerialName("currency")
    val currency: String = "",
    @SerialName("currentPrice")
    val currentPrice: Double = 0.0,
)
