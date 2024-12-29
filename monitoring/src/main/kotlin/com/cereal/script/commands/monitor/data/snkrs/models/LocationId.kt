package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationId(
    @SerialName("id")
    val id: String = "",
    @SerialName("type")
    val type: String = "",
)
