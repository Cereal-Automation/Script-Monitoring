package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductRollup(
    @SerialName("key")
    val key: String = "",
    @SerialName("type")
    val type: String = "",
)
