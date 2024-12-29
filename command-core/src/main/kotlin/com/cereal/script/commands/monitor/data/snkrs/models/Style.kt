package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Style(
    @SerialName("defaultStyle")
    val defaultStyle: DefaultStyle? = DefaultStyle(),
    @SerialName("exposeTemplate")
    val exposeTemplate: Boolean = false,
    @SerialName("modifiedDate")
    val modifiedDate: String = "",
    @SerialName("properties")
    val properties: PropertiesX = PropertiesX(),
    @SerialName("resourceType")
    val resourceType: String = "",
)
