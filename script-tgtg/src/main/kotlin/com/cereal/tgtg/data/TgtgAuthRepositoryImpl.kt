package com.cereal.tgtg.data

import com.cereal.command.monitor.data.tgtg.apiclients.TgtgApiClient
import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import com.cereal.tgtg.domain.AuthByEmailResult
import com.cereal.tgtg.domain.TgtgAuthRepository

/**
 * Implementation of TgtgAuthRepository that uses TgtgApiClient internally.
 */
class TgtgAuthRepositoryImpl(
    private val tgtgApiClient: TgtgApiClient,
    private val userInteractionComponent: UserInteractionComponent,
) : TgtgAuthRepository {
    override suspend fun login(): Boolean = tgtgApiClient.login()

    override suspend fun authByEmail(email: String): AuthByEmailResult {
        var attempts = 0
        while (attempts < 3) {
            attempts++
            val response = tgtgApiClient.authByEmail(email)
            val captchaUrl: String? = response.captchaUrl
            if (captchaUrl != null) {
                val challengeUrl: String = captchaUrl
                val result =
                    userInteractionComponent.showUrl(
                        title = "Solve TGTG Captcha",
                        url = challengeUrl,
                    ) { nav ->
                        nav.url.contains("/captcha/check")
                    }
                val success = captchaCheck(result.url)
                if (!success) throw Exception("Failed to complete captcha challenge")
                continue // retry authentication after solving captcha
            }
            val pollingId = response.pollingId ?: throw Exception("No polling ID received from authentication request")
            return AuthByEmailResult(pollingId = pollingId)
        }
        throw Exception("Failed to obtain polling ID after captcha attempts")
    }

    override suspend fun captchaCheck(fullUrl: String): Boolean = tgtgApiClient.executeCaptchaCheck(fullUrl)

    override suspend fun authPoll(
        pollingId: String,
        email: String,
    ): Boolean {
        val response = tgtgApiClient.authPoll(pollingId, email)
        return response?.accessToken != null && response.refreshToken != null
    }
}
