package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Analytics(
    @SerialName("hashKey")
    val hashKey: String = "",
)
