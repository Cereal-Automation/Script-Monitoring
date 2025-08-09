package com.cereal.command.monitor.data.tgtg

import kotlinx.serialization.Serializable
import java.util.UUID

data class TgtgConfig(
    var correlationId: String = UUID.randomUUID().toString(),
    val deviceType: String = "ANDROID",
    val email: String,
    var appVersion: String = "23.2.1",
    var session: TgtgSession? = null,
)

@Serializable
data class TgtgSession(
    val accessToken: String? = null,
    val refreshToken: String? = null,
)
