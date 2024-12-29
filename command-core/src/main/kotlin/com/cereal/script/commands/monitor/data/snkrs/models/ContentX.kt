package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentX(
    @SerialName("text")
    val text: String = "",
    @SerialName("type")
    val type: String = "",
)
