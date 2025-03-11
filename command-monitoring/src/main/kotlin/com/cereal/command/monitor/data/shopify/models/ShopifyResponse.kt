package com.cereal.command.monitor.data.shopify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopifyResponse(
    @SerialName("products")
    val products: List<Product> = emptyList(),
)
