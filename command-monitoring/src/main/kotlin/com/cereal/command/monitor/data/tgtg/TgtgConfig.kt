package com.cereal.command.monitor.data.tgtg

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TgtgConfig(
    val correlationId: String = UUID.randomUUID().toString(),
    val deviceType: String = "ANDROID",
    val session: TgtgSession? = null,
    // Cookie provided after solving captcha challenge; send with subsequent requests if present
    val datadomeCookie: String? = null,
)

@Serializable
data class TgtgSession(
    val accessToken: String? = null,
    val refreshToken: String? = null,
)
