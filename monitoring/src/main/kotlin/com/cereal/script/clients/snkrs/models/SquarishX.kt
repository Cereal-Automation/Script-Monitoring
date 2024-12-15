package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SquarishX(
    @SerialName("aspectRatio")
    val aspectRatio: Double? = null,
    @SerialName("id")
    val id: String = "",
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("view")
    val view: String? = null,
)
