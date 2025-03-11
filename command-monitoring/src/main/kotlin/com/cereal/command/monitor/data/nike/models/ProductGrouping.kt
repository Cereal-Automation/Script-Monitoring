package com.cereal.command.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductGrouping(
    @SerialName("cardType")
    val cardType: String = "",
    @SerialName("products")
    val products: List<Product>? = listOf(),
)
