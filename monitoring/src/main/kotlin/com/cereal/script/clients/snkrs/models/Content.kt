package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    @SerialName("content")
    val content: List<ContentX> = listOf(),
    @SerialName("type")
    val type: String = "",
)
