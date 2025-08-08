package com.cereal.tgtg.domain

import com.cereal.command.monitor.data.tgtg.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.models.AuthPollResponse

/**
 * Repository interface for TGTG authentication operations.
 *
 */
interface TgtgAuthRepository {

    /**
     * Attempts to login using existing stored credentials (refresh token).
     *
     * @return true if login was successful, false if no credentials are available or login failed
     */
    suspend fun login(): Boolean

    /**
     * Initiates the email-based authentication process by sending an authentication email.
     *
     * @return AuthByEmailResponse containing the polling ID needed for polling authentication status
     * @throws Exception if the authentication email could not be sent
     */
    suspend fun authByEmail(): AuthByEmailResponse

    /**
     * Polls for authentication completion using the polling ID from authByEmail().
     *
     * @param pollingId The polling ID obtained from authByEmail()
     * @return AuthPollResponse containing access and refresh tokens if authentication is complete
     * @throws Exception if polling fails
     */
    suspend fun authPoll(pollingId: String): AuthPollResponse
}