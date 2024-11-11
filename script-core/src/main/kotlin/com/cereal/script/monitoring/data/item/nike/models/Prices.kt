package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Prices(
    @SerialName("currency")
    val currency: String = "",
    @SerialName("currentPrice")
    val currentPrice: Double = 0.0,
    @SerialName("discountPercentage")
    val discountPercentage: Int = 0,
    @SerialName("employeeDiscountPercentage")
    val employeeDiscountPercentage: Int = 0,
    @SerialName("employeePrice")
    val employeePrice: Double = 0.0,
    @SerialName("initialPrice")
    val initialPrice: Double = 0.0,
)
