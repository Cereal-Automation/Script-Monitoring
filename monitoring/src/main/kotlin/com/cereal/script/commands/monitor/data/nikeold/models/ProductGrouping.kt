package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductGrouping(
    @SerialName("cardType")
    val cardType: String = "",
    @SerialName("products")
    val products: List<Product>? = listOf(),
    @SerialName("properties")
    val properties: Properties? =
        Properties(),
)
