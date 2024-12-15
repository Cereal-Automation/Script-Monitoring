package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonBody(
    @SerialName("content")
    val content: List<Content> = listOf(),
    @SerialName("type")
    val type: String = "",
)
