package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimpleColor(
    @SerialName("hex")
    val hex: String = "",
    @SerialName("label")
    val label: String = "",
)
