package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Squarish(
    @SerialName("aspectRatio")
    val aspectRatio: Int? = null,
    @SerialName("id")
    val id: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("view")
    val view: String? = null,
)
