package com.cereal.command.monitor.data.stockx

import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
data class OAuthTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int, // in seconds
    val refresh_token: String?
)

class OAuthTokenProvider(
    private val userInteractionComponent: UserInteractionComponent,
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String,
    private val tokenUrl: String,
    private val authorizationUrl: String,
    private var refreshToken: String? = null
) {
    private var currentToken: String? = null
    private var expiresAt: Long = 0L

    suspend fun fetchTokenWithAuthorizationCode(code: String): String = withContext(Dispatchers.IO) {
        val body = FormBody.Builder()
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("code", code)
            .add("redirect_uri", redirectUri)
            .add("grant_type", "authorization_code")
            .build()

        val tokenRequest = Request.Builder()
            .url(tokenUrl)
            .post(body)
            .build()

        val tokenResponse = OkHttpClient().newCall(tokenRequest).execute()

        if (!tokenResponse.isSuccessful) throw Exception("Failed to fetch token")

        val json = tokenResponse.body?.string() ?: ""
        val tokenData = Json.decodeFromString<OAuthTokenResponse>(json)

        currentToken = tokenData.access_token
        refreshToken = tokenData.refresh_token
        expiresAt = System.currentTimeMillis() + (tokenData.expires_in * 1000)

        currentToken!!
    }

    suspend fun refreshToken(): String? = withContext(Dispatchers.IO) {
        val body = FormBody.Builder()
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("refresh_token", refreshToken ?: "")
            .add("grant_type", "refresh_token")
            .build()

        val tokenRequest = Request.Builder()
            .url(tokenUrl)
            .post(body)
            .build()

        val tokenResponse = OkHttpClient().newCall(tokenRequest).execute()

        if (!tokenResponse.isSuccessful) throw Exception("Failed to refresh token")

        val json = tokenResponse.body?.string() ?: ""
        val tokenData = Json.decodeFromString<OAuthTokenResponse>(json)

        currentToken = tokenData.access_token
        refreshToken = tokenData.refresh_token
        expiresAt = System.currentTimeMillis() + (tokenData.expires_in * 1000)

        currentToken!!
    }

    suspend fun getToken(): String? {
        // If no token, initiate the Authorization Code Flow or refresh token if expired
        if (currentToken == null || System.currentTimeMillis() > expiresAt) {
            return if (refreshToken != null) {
                refreshToken()
            } else {
                requestUserAuthorization()
            }
        }
        return currentToken
    }

    suspend fun requestUserAuthorization(): String {
        val authUrl = "$authorizationUrl?client_id=$clientId&redirect_uri=$redirectUri&response_type=code&scope=read"
        val result = userInteractionComponent.showUrl("Authenticate with StockX", authUrl) {
            it.url.startsWith(redirectUri)
        }

        val httpUrl = result.url.toHttpUrl()

        val code = httpUrl.queryParameter("code") ?: throw Exception("Failed to get auth code")
        return fetchTokenWithAuthorizationCode(code)
    }

}
