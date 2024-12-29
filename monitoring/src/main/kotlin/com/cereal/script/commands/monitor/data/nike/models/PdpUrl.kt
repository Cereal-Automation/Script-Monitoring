package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PdpUrl(
    @SerialName("url")
    val url: String = "",
)
