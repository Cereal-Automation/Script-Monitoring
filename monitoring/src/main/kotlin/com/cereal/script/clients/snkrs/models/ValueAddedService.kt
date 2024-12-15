package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValueAddedService(
    @SerialName("id")
    val id: String = "",
    @SerialName("vasType")
    val vasType: String = "",
)
