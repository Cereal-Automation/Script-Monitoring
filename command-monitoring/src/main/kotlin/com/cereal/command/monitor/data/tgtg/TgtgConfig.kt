package com.cereal.command.monitor.data.tgtg

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TgtgConfig(
    val correlationId: String = UUID.randomUUID().toString(),
    val deviceType: String = "ANDROID",
    val session: TgtgSession? = null,
)

@Serializable
data class TgtgSession(
    val accessToken: String? = null,
    val refreshToken: String? = null,
)
