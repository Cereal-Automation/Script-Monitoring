package com.cereal.command.monitor.data.shopify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Option(
    @SerialName("name")
    val name: String? = null,
    @SerialName("position")
    val position: Int? = null,
    @SerialName("values")
    val values: List<String?>? = null,
)
