package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductX(
    @SerialName("productId")
    val productId: String = "",
    @SerialName("styleColor")
    val styleColor: String = "",
)
