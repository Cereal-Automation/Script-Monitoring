package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VariableX(
    @SerialName("id")
    val id: String = "",
    @SerialName("value")
    val value: String = "",
)
