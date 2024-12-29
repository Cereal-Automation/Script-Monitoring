package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    @SerialName("product")
    val product: ProductX? = ProductX(),
    @SerialName("type")
    val type: String = "",
    @SerialName("url")
    val url: String? = "",
)
