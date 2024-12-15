package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SnkrsResponse(
    @SerialName("objects")
    val objects: List<Object> = listOf(),
    @SerialName("pages")
    val pages: Pages = Pages(),
)
