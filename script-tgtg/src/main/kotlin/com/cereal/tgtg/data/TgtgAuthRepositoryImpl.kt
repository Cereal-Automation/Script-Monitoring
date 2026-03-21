package com.cereal.tgtg.data

import com.cereal.command.monitor.data.tgtg.apiclients.TgtgApiClient
import com.cereal.tgtg.domain.AuthByEmailResult
import com.cereal.tgtg.domain.TgtgAuthRepository

/**
 * Implementation of TgtgAuthRepository that uses TgtgApiClient internally.
 */
class TgtgAuthRepositoryImpl(
    private val tgtgApiClient: TgtgApiClient,
) : TgtgAuthRepository {
    override suspend fun login(): Boolean = tgtgApiClient.login()

    override suspend fun authByEmail(email: String): AuthByEmailResult {
        val response = tgtgApiClient.authByEmail(email)
        if (response.captchaUrl != null) {
            throw Exception(
                "Too Good To Go requires a CAPTCHA verification before login can proceed. " +
                    "This usually happens after too many login attempts. " +
                    "Please wait a while and try again later.",
            )
        }
        val pollingId =
            response.pollingId
                ?: throw Exception("No polling ID received from authentication request")
        return AuthByEmailResult(pollingId = pollingId)
    }

    override suspend fun authPoll(
        pollingId: String,
        email: String,
    ): Boolean {
        val response = tgtgApiClient.authPoll(pollingId, email)
        return response?.accessToken != null && response.refreshToken != null
    }

    override suspend fun authByRequestPin(
        pollingId: String,
        pin: String,
        email: String,
    ): Boolean {
        val response = tgtgApiClient.authByRequestPin(pollingId, pin, email)
        return response?.accessToken != null && response.refreshToken != null
    }
}
