package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesX(
    @SerialName("body")
    val body: Body = Body(),
    @SerialName("btnStyleTrait")
    val btnStyleTrait: String = "",
    @SerialName("title")
    val title: Title = Title(),
)
