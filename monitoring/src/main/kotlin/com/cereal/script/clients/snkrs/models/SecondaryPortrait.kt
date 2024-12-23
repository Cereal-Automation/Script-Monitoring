package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecondaryPortrait(
    @SerialName("id")
    val id: String? = null,
    @SerialName("url")
    val url: String? = null,
)
