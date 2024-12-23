package com.cereal.script.clients.toogoodtogo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("device_type") val deviceType: String,
    @SerialName("email") val email: String,
)
