package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationId(
    @SerialName("id")
    val id: String = "",
    @SerialName("type")
    val type: String = "",
)
