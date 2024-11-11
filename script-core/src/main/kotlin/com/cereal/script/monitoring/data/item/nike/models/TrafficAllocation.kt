package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrafficAllocation(
    @SerialName("endOfRange")
    val endOfRange: Int = 0,
    @SerialName("entityId")
    val entityId: String = "",
)
