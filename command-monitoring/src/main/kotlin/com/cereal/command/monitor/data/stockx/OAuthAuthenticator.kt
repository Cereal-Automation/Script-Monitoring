package com.cereal.command.monitor.data.stockx

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class OAuthAuthenticator(
    private val tokenProvider: OAuthTokenProvider,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? =
        runBlocking {
            // Avoid infinite retry loops by checking if we just refreshed
            val previousAuthHeader = response.request.header("Authorization")
            if (previousAuthHeader != null && previousAuthHeader.startsWith("Bearer ") && response.code == 401) {
                // If we already tried with a token and still got 401, give up
                null
            } else {
                // Try to refresh the token using the refresh token
                tokenProvider.refreshToken()?.let {
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
                }
            }
        }
}
