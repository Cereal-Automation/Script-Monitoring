package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrafficAllocation(
    @SerialName("endOfRange")
    val endOfRange: Int = 0,
    @SerialName("entityId")
    val entityId: String = "",
)
