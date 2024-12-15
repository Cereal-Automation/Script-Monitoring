package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentX(
    @SerialName("text")
    val text: String = "",
    @SerialName("type")
    val type: String = "",
)
