package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesX(
    @SerialName("actions")
    val actions: Actions? = null,
)
