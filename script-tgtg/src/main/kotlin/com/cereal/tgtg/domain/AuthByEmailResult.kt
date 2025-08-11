package com.cereal.tgtg.domain

/**
 * Domain model representing the result of an email-based authentication request.
 * Contains the polling ID needed for checking authentication status.
 */
data class AuthByEmailResult(
    val pollingId: String,
)