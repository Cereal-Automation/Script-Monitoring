package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Color(
    @SerialName("hex")
    val hex: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("type")
    val type: String = ""
)