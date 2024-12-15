package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Landscape(
    @SerialName("aspectRatio")
    val aspectRatio: Int? = null,
    @SerialName("id")
    val id: String = "",
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("view")
    val view: String? = null,
)
