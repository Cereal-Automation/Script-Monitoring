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
            // If we already tried to refresh the token, give up
            if (response.code == 401) {
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
