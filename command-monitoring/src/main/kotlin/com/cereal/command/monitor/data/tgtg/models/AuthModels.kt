package com.cereal.command.monitor.data.tgtg.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthByEmailRequest(
    @SerialName("device_type")
    val deviceType: String,
    @SerialName("email")
    val email: String,
)

@Serializable
data class AuthByEmailResponse(
    @SerialName("polling_id")
    val pollingId: String? = null,
    @SerialName("state")
    val state: String? = null,
)

@Serializable
data class AuthPollRequest(
    @SerialName("device_type")
    val deviceType: String,
    @SerialName("email")
    val email: String,
    @SerialName("request_polling_id")
    val requestPollingId: String,
)

@Serializable
data class AuthPollResponse(
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("startup_data")
    val startupData: StartupData? = null,
)

@Serializable
data class StartupData(
    @SerialName("user")
    val user: User? = null,
)

@Serializable
data class User(
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("email")
    val email: String? = null,
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token")
    val refreshToken: String,
)

@Serializable
data class RefreshTokenResponse(
    @SerialName("access_token")
    val accessToken: String? = null,
)
