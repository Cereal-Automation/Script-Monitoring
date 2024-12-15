package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Title(
    @SerialName("fontFamily")
    val fontFamily: String = "",
    @SerialName("fontSize")
    val fontSize: String = "",
    @SerialName("fontStyle")
    val fontStyle: String = "",
)
