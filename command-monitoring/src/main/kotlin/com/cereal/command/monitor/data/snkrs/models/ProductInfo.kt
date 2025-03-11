package com.cereal.command.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductInfo(
    @SerialName("availability")
    val availability: Availability = Availability(),
    @SerialName("availableGtins")
    val availableGtins: List<AvailableGtin> = listOf(),
    @SerialName("merchPrice")
    val merchPrice: MerchPrice = MerchPrice(),
    @SerialName("merchProduct")
    val merchProduct: MerchProduct = MerchProduct(),
    @SerialName("productContent")
    val productContent: ProductContent = ProductContent(),
    @SerialName("skus")
    val skus: List<Sku> = listOf(),
)
