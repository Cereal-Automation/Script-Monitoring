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
     * May return a captchaUrl instead of a pollingId if a captcha challenge must be solved first.
     *
     * @return AuthByEmailResult containing pollingId OR captchaUrl
     * @throws Exception if the authentication request fails
     */
    suspend fun authByEmail(email: String): AuthByEmailResult

    /**
     * Executes the captcha check URL after intercepting navigation to the captcha/check endpoint.
     * Stores any returned cookie for subsequent requests.
     */
    suspend fun captchaCheck(fullUrl: String): Boolean

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
