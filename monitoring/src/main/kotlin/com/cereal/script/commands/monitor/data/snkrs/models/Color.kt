package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Color(
    @SerialName("hex")
    val hex: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("type")
    val type: String = "",
)
