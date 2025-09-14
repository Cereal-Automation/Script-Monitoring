package com.cereal.command.monitor.data.tgtg.apiclients.exception

/**
 * Exception thrown when the TGTG app version cannot be determined.
 */
class TgtgAppVersionException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
