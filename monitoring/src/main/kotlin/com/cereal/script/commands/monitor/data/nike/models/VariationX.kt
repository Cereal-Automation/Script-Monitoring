package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VariationX(
    @SerialName("featureEnabled")
    val featureEnabled: Boolean = false,
    @SerialName("id")
    val id: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("variables")
    val variables: List<VariableX> = listOf(),
)
