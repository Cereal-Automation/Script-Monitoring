package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoverCard(
    @SerialName("analytics")
    val analytics: Analytics = Analytics(),
    @SerialName("id")
    val id: String = "",
    @SerialName("properties")
    val properties: PropertiesXXXXX = PropertiesXXXXX(),
    @SerialName("subType")
    val subType: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("version")
    val version: String = ""
)