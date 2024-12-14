package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomXXX(
    @SerialName("restricted")
    val restricted: Boolean? = null,
    @SerialName("tags")
    val tags: List<String>? = null,
)
