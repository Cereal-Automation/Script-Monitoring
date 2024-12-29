package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonBody(
    @SerialName("content")
    val content: List<Content> = listOf(),
    @SerialName("type")
    val type: String = "",
)
