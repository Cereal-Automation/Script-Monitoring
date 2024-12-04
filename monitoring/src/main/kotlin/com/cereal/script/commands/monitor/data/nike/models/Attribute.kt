package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attribute(
    @SerialName("id")
    val id: String = "",
    @SerialName("key")
    val key: String = "",
)
