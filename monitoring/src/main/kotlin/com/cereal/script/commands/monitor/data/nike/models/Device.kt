package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Device(
    @SerialName("deviceWidth")
    val deviceWidth: String = "",
)
