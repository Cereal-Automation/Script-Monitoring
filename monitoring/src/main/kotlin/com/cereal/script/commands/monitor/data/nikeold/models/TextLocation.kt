package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextLocation(
    @SerialName("horizontal")
    val horizontal: String = "",
    @SerialName("vertical")
    val vertical: String = "",
)
