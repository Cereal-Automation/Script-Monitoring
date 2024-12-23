package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableGtin(
    @SerialName("available")
    val available: Boolean = false,
    @SerialName("gtin")
    val gtin: String = "",
    @SerialName("level")
    val level: String = "",
    @SerialName("locationId")
    val locationId: LocationId = LocationId(),
    @SerialName("method")
    val method: String = "",
    @SerialName("styleColor")
    val styleColor: String = "",
    @SerialName("styleType")
    val styleType: String? = null,
)
