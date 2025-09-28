package com.cereal.tgtg.command.context

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Data class to store authentication state in ChainContext between commands.
 */
@OptIn(ExperimentalTime::class)
data class TgtgAuthState(
    val pollingId: String,
    val startTime: Instant,
    val instructionsShown: Boolean = false,
)
