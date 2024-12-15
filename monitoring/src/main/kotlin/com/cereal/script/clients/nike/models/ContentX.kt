package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentX(
    @SerialName("text")
    val text: String = "",
    @SerialName("type")
    val type: String = "",
)
