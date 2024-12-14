package com.cereal.script.commands.monitor.data.nikeold.models

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
