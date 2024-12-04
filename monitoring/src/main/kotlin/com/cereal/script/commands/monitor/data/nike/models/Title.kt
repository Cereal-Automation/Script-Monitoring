package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Title(
    @SerialName("fontFamily")
    val fontFamily: String = "",
    @SerialName("textColor")
    val textColor: String = "",
)
