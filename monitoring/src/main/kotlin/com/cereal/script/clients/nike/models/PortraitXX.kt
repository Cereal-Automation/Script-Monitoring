package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortraitXX(
    @SerialName("id")
    val id: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("url")
    val url: String = "",
)
