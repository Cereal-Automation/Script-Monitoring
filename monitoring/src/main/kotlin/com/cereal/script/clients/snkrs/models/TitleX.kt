package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TitleX(
    @SerialName("fontFamily")
    val fontFamily: String? = null,
    @SerialName("fontSize")
    val fontSize: String? = null,
    @SerialName("fontStyle")
    val fontStyle: String? = null,
    @SerialName("textColor")
    val textColor: String? = null,
)
