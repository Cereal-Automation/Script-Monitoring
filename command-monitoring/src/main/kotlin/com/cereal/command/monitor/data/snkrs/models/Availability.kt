package com.cereal.command.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Availability(
    @SerialName("available")
    val available: Boolean = false,
    @SerialName("id")
    val id: String = "",
    @SerialName("productId")
    val productId: String = "",
    @SerialName("resourceType")
    val resourceType: String = "",
)
