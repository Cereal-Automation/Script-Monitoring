package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FocalPoint(
    @SerialName("hardCrop")
    val hardCrop: Boolean = false,
    @SerialName("horizontal")
    val horizontal: String = "",
    @SerialName("vertical")
    val vertical: String = ""
)