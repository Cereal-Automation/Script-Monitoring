package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValueAddedService(
    @SerialName("id")
    val id: String = "",
    @SerialName("vasType")
    val vasType: String = "",
)
