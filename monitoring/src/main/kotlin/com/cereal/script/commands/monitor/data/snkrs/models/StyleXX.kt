package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StyleXX(
    @SerialName("defaultStyle")
    val defaultStyle: DefaultStyleXX? = DefaultStyleXX(),
    @SerialName("exposeTemplate")
    val exposeTemplate: Boolean = false,
    @SerialName("modifiedDate")
    val modifiedDate: String = "",
    @SerialName("properties")
    val properties: PropertiesXXXXXX = PropertiesXXXXXX(),
    @SerialName("resourceType")
    val resourceType: String = "",
    @SerialName("templateId")
    val templateId: String? = "",
)
