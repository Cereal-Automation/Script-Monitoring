package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Variation(
    @SerialName("featureEnabled")
    val featureEnabled: Boolean = false,
    @SerialName("id")
    val id: String = "",
    @SerialName("key")
    val key: String = "",
)
