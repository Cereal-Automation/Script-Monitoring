package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Audience(
    @SerialName("conditions")
    val conditions: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
)
