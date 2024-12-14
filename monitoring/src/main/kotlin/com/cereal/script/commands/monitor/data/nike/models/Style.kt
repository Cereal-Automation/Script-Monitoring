package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Style(
    @SerialName("defaultStyle")
    val defaultStyle: DefaultStyle =
        DefaultStyle(),
    @SerialName("properties")
    val properties: PropertiesX =
        PropertiesX(),
)
