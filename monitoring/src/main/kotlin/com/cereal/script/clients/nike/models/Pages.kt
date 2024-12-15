package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pages(
    @SerialName("next")
    val next: String = "",
    @SerialName("prev")
    val prev: String = "",
    @SerialName("totalPages")
    val totalPages: Int = 0,
    @SerialName("totalResources")
    val totalResources: Int = 0,
)
