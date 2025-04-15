package com.cereal.command.monitor.data.zalando.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Brand(
    @SerialName("name")
    val name: String = "",
    @SerialName("@type")
    val type: String = "",
)
