package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExperienceText(
    @SerialName("fontFamily")
    val fontFamily: String = "",
    @SerialName("fontSize")
    val fontSize: String = "",
    @SerialName("fontStyle")
    val fontStyle: String = "",
)
