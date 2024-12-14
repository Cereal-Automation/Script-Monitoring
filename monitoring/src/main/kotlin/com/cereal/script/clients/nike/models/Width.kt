package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Width(
    @SerialName("localizedValue")
    val localizedValue: String = "",
    @SerialName("value")
    val value: String = ""
)