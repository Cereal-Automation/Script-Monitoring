package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Width(
    @SerialName("localizedValue")
    val localizedValue: String = "",
    @SerialName("value")
    val value: String = "",
)
