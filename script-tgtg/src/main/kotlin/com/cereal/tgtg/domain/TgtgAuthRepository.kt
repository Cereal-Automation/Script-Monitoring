package com.cereal.tgtg.domain

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
     * @return AuthByEmailResult containing the polling ID needed for polling authentication status
     * @throws Exception if the authentication email could not be sent
     */
    suspend fun authByEmail(email: String): AuthByEmailResult

    /**
     * Polls for authentication completion using the polling ID from authByEmail().
     *
     * @param pollingId The polling ID obtained from authByEmail()
     * @param email The email address used for authentication
     * @return true if authentication is complete with valid tokens, false otherwise
     * @throws Exception if polling fails
     */
    suspend fun authPoll(
        pollingId: String,
        email: String,
    ): Boolean
}
