package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeoCopyBlock(
    @SerialName("body")
    val body: String = "",
    @SerialName("title")
    val title: String = "",
)
