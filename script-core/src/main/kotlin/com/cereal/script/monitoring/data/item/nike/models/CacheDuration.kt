package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CacheDuration(
    @SerialName("default")
    val default: Int = 0,
    @SerialName("perf")
    val perf: Int = 0,
    @SerialName("wall")
    val wall: Int = 0,
    @SerialName("404")
    val x404: Int = 0,
)
