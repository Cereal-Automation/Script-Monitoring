package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VariableX(
    @SerialName("id")
    val id: String = "",
    @SerialName("value")
    val value: String = "",
)
