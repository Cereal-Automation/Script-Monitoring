package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StyleX(
    @SerialName("defaultStyle")
    val defaultStyle: DefaultStyleX? = DefaultStyleX(),
    @SerialName("exposeTemplate")
    val exposeTemplate: Boolean = false,
    @SerialName("modifiedDate")
    val modifiedDate: String = "",
    @SerialName("properties")
    val properties: PropertiesXXX = PropertiesXXX(),
    @SerialName("resourceType")
    val resourceType: String = "",
    @SerialName("templateId")
    val templateId: String? = ""
)