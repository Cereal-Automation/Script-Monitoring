package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    @SerialName("content")
    val content: List<ContentX> = listOf(),
    @SerialName("type")
    val type: String = "",
)
