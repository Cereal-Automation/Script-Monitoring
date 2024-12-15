package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsumerChannel(
    @SerialName("id")
    val id: String = "",
    @SerialName("resourceType")
    val resourceType: String = "",
)
