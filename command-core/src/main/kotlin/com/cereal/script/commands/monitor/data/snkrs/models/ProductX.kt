package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductX(
    @SerialName("productId")
    val productId: String = "",
    @SerialName("styleColor")
    val styleColor: String = "",
)
