package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Body(
    @SerialName("fontFamily")
    val fontFamily: String = "",
    @SerialName("textColor")
    val textColor: String = "",
)
