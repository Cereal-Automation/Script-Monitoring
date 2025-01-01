package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SnkrsResponse(
    @SerialName("objects")
    val objects: List<Object> = listOf(),
)
