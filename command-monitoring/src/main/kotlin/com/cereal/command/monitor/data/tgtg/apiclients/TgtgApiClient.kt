package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.tgtg.TgtgConfig
import com.cereal.command.monitor.data.tgtg.TgtgSession
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.FavoriteBusinessesRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.FavoriteBusinessesResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenResponse
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TgtgApiClient(
    private val logRepository: LogRepository,
    private val preferenceComponent: PreferenceComponent,
    private val playStoreApiClient: PlayStoreApiClient,
    private val httpProxy: Proxy? = null,
    private val timeout: Duration = 30.seconds,
) {
    private val baseUrl = "https://apptoogoodtogo.com/api/"
    private val json = defaultJson()

    private val configKey = "tgtg_config"

    private suspend fun getTgtgConfig(): TgtgConfig {
        val configJson = preferenceComponent.getString(configKey)
        return if (configJson != null) {
            try {
                json.decodeFromString<TgtgConfig>(configJson)
            } catch (e: Exception) {
                logRepository.debug("Failed to deserialize stored config: ${e.message}")
                TgtgConfig()
            }
        } else {
            TgtgConfig()
        }
    }

    private suspend fun storeTgtgConfig(config: TgtgConfig) {
        val configJson = json.encodeToString(config)
        preferenceComponent.setString(configKey, configJson)
    }

    private suspend fun createHttpClient(): HttpClient {
        val appVersion = playStoreApiClient.getAppVersion()
        val headers =
            mapOf(
                HttpHeaders.ContentType to ContentType.Application.Json.toString(),
                HttpHeaders.Accept to "application/json",
                HttpHeaders.AcceptLanguage to "en-US",
                HttpHeaders.AcceptEncoding to "gzip",
                HttpHeaders.UserAgent to "TGTG/$appVersion Dalvik/2.1.0 (Linux; Android 12; SM-G920V Build/MMB29K)",
                "x-correlation-id" to getTgtgConfig().correlationId,
            )

        return defaultHttpClient(
            timeout = timeout,
            httpProxy = httpProxy,
            logRepository = logRepository,
            defaultHeaders = headers,
        )
    }

    suspend fun authByEmail(email: String): AuthByEmailResponse {
        val currentConfig = getTgtgConfig()
        val updatedConfig = currentConfig.copy(correlationId = UUID.randomUUID().toString())
        storeTgtgConfig(updatedConfig)

        val request =
            AuthByEmailRequest(
                deviceType = updatedConfig.deviceType,
                email = email,
            )

        val httpClient = createHttpClient()
        val response =
            httpClient.post("${baseUrl}auth/v5/authByEmail") {
                setBody(json.encodeToString(AuthByEmailRequest.serializer(), request))
            }

        val bodyText = response.bodyAsText()
        return json.decodeFromString(AuthByEmailResponse.serializer(), bodyText)
    }

    suspend fun authPoll(
        pollingId: String,
        email: String,
    ): AuthPollResponse {
        val currentConfig = getTgtgConfig()
        val request =
            AuthPollRequest(
                deviceType = currentConfig.deviceType,
                email = email,
                requestPollingId = pollingId,
            )

        val httpClient = createHttpClient()
        val response =
            httpClient.post("${baseUrl}auth/v5/authByRequestPollingId") {
                setBody(json.encodeToString(AuthPollRequest.serializer(), request))
            }

        val bodyText = response.bodyAsText()
        val authResponse = json.decodeFromString(AuthPollResponse.serializer(), bodyText)

        // Create session if authentication was successful
        authResponse.accessToken?.let { accessToken ->
            authResponse.refreshToken?.let { refreshToken ->
                createSession(accessToken, refreshToken)
            }
        }

        return authResponse
    }

    suspend fun login(): Boolean {
        val currentConfig = getTgtgConfig()
        val updatedConfig = currentConfig.copy(correlationId = UUID.randomUUID().toString())
        storeTgtgConfig(updatedConfig)

        val session = updatedConfig.session
        return if (session?.refreshToken != null) {
            refreshToken()
        } else {
            logRepository.info("No refresh token available. Please authenticate first.")
            false
        }
    }

    private suspend fun refreshToken(): Boolean {
        val config = getTgtgConfig()
        val refreshToken = config.session?.refreshToken ?: return false

        val request = RefreshTokenRequest(refreshToken = refreshToken)

        val httpClient = createHttpClient()
        val response =
            httpClient.post("${baseUrl}token/v1/refresh") {
                setBody(json.encodeToString(RefreshTokenRequest.serializer(), request))
            }

        val bodyText = response.bodyAsText()
        val tokenResponse = json.decodeFromString(RefreshTokenResponse.serializer(), bodyText)

        tokenResponse.accessToken?.let { accessToken ->
            updateSession(accessToken)
            return true
        }

        return false
    }

    suspend fun listFavoriteBusinesses(request: FavoriteBusinessesRequest): FavoriteBusinessesResponse? {
        val config = getTgtgConfig()
        val session = config.session
        if (session?.refreshToken == null) {
            logRepository.info("You are not logged in. Login via authByEmail and authPoll first.")
            return null
        }

        val httpClient = createHttpClient()
        val response =
            httpClient.post("${baseUrl}item/v8/") {
                headers[HttpHeaders.Authorization] = "Bearer ${session.accessToken}"
                setBody(json.encodeToString(FavoriteBusinessesRequest.serializer(), request))
            }

        val bodyText = response.bodyAsText()
        return json.decodeFromString(FavoriteBusinessesResponse.serializer(), bodyText)
    }

    private suspend fun createSession(
        accessToken: String,
        refreshToken: String,
    ) {
        val currentConfig = getTgtgConfig()
        val session =
            TgtgSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        val updatedConfig = currentConfig.copy(session = session)
        storeTgtgConfig(updatedConfig)
    }

    private suspend fun updateSession(accessToken: String) {
        val currentConfig = getTgtgConfig()
        val currentSession = currentConfig.session
        if (currentSession != null) {
            val updatedSession = currentSession.copy(accessToken = accessToken)
            val updatedConfig = currentConfig.copy(session = updatedSession)
            storeTgtgConfig(updatedConfig)
        }
    }
}
