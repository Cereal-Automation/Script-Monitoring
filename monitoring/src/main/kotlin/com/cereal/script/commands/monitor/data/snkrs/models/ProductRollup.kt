package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductRollup(
    @SerialName("key")
    val key: String = "",
    @SerialName("type")
    val type: String = "",
)
