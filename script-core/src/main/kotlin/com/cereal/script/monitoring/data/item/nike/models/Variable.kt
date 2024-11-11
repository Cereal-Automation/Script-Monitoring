package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Variable(
    @SerialName("defaultValue")
    val defaultValue: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("type")
    val type: String = "",
)
