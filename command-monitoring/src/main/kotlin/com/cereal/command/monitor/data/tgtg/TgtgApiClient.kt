package com.cereal.command.monitor.data.tgtg

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.tgtg.models.AuthByEmailRequest
import com.cereal.command.monitor.data.tgtg.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.models.AuthPollRequest
import com.cereal.command.monitor.data.tgtg.models.AuthPollResponse
import com.cereal.command.monitor.data.tgtg.models.FavoriteBusinessesRequest
import com.cereal.command.monitor.data.tgtg.models.FavoriteBusinessesResponse
import com.cereal.command.monitor.data.tgtg.models.RefreshTokenRequest
import com.cereal.command.monitor.data.tgtg.models.RefreshTokenResponse
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TgtgApiClient(
    private val logRepository: LogRepository,
    private val config: TgtgConfig,
    private val preferenceComponent: PreferenceComponent,
    private val httpProxy: Proxy? = null,
    private val timeout: Duration = 30.seconds,
) {
    private val baseUrl = "https://apptoogoodtogo.com/api/"
    private val json = defaultJson()
    private val sessionMutex = Mutex()

    // Preference keys for persistent storage
    private val correlationIdKey = "tgtg_correlation_id_${config.email}"
    private val sessionKey = "tgtg_session_${config.email}"

    private val defaultHeaders =
        mapOf(
            HttpHeaders.ContentType to ContentType.Application.Json.toString(),
            HttpHeaders.Accept to "application/json",
            HttpHeaders.AcceptLanguage to "en-US",
            HttpHeaders.AcceptEncoding to "gzip",
            HttpHeaders.UserAgent to "TGTG/${config.appVersion} Dalvik/2.1.0 (Linux; Android 12; SM-G920V Build/MMB29K)",
        )

    private suspend fun createHttpClient(): HttpClient {
        val headers = defaultHeaders.toMutableMap()
        headers["x-correlation-id"] = getCorrelationId()

        return defaultHttpClient(
            timeout = timeout,
            httpProxy = httpProxy,
            logRepository = logRepository,
            defaultHeaders = headers,
        )
    }

    // Helper methods for persistent storage
    private suspend fun getCorrelationId(): String {
        return preferenceComponent.getString(correlationIdKey) ?: UUID.randomUUID().toString().also {
            preferenceComponent.setString(correlationIdKey, it)
        }
    }

    private suspend fun setCorrelationId(correlationId: String) {
        preferenceComponent.setString(correlationIdKey, correlationId)
    }

    private suspend fun getStoredSession(): TgtgSession =
        sessionMutex.withLock {
            val sessionJson = preferenceComponent.getString(sessionKey)
            if (sessionJson != null) {
                try {
                    json.decodeFromString<TgtgSession>(sessionJson)
                } catch (e: Exception) {
                    logRepository.debug("Failed to deserialize stored session: ${e.message}")
                    TgtgSession()
                }
            } else {
                TgtgSession()
            }
        }

    private suspend fun storeSession(session: TgtgSession) =
        sessionMutex.withLock {
            val sessionJson = json.encodeToString(session)
            preferenceComponent.setString(sessionKey, sessionJson)
        }

    suspend fun authByEmail(): AuthByEmailResponse {
        setCorrelationId(UUID.randomUUID().toString())

        val request =
            AuthByEmailRequest(
                deviceType = config.deviceType,
                email = config.email,
            )

        val httpClient = createHttpClient()
        val response =
            httpClient.post("${baseUrl}auth/v5/authByEmail") {
                setBody(json.encodeToString(AuthByEmailRequest.serializer(), request))
            }

        val bodyText = response.bodyAsText()
        return json.decodeFromString(AuthByEmailResponse.serializer(), bodyText)
    }

    suspend fun authPoll(pollingId: String): AuthPollResponse {
        val request =
            AuthPollRequest(
                deviceType = config.deviceType,
                email = config.email,
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
        setCorrelationId(UUID.randomUUID().toString())

        val session = getStoredSession()
        return if (session.refreshToken != null) {
            refreshToken()
        } else {
            logRepository.info("No refresh token available. Please authenticate first.")
            false
        }
    }

    private suspend fun refreshToken(): Boolean {
        val session = getStoredSession()
        val refreshToken = session.refreshToken ?: return false

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

    suspend fun listFavoriteBusinesses(): FavoriteBusinessesResponse? {
        val request =
            FavoriteBusinessesRequest(
                favoritesOnly = true,
                origin =
                    FavoriteBusinessesRequest.Origin(
                        latitude = 52.5170365,
                        longitude = 13.3888599,
                    ),
                radius = 200,
            )
        return listFavoriteBusinesses(request)
    }

    suspend fun listFavoriteBusinesses(request: FavoriteBusinessesRequest): FavoriteBusinessesResponse? {
        val session = getStoredSession()
        if (session.refreshToken == null) {
            logRepository.info("You are not logged in. Login via authByEmail and authPoll first.")
            return null
        }

        val headers = defaultHeaders.toMutableMap()
        headers["x-correlation-id"] = getCorrelationId()
        headers[HttpHeaders.Authorization] = "Bearer ${session.accessToken}"

        val httpClient =
            defaultHttpClient(
                timeout = timeout,
                httpProxy = httpProxy,
                logRepository = logRepository,
                defaultHeaders = headers,
            )

        val response =
            httpClient.post("${baseUrl}item/v8/") {
                setBody(json.encodeToString(FavoriteBusinessesRequest.serializer(), request))
            }

        val bodyText = response.bodyAsText()
        return json.decodeFromString(FavoriteBusinessesResponse.serializer(), bodyText)
    }

    private suspend fun getSession(): TgtgSession = getStoredSession()

    private suspend fun createSession(
        accessToken: String,
        refreshToken: String,
    ) {
        val session =
            TgtgSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        storeSession(session)
    }

    private suspend fun updateSession(accessToken: String) {
        val currentSession = getStoredSession()
        val updatedSession = currentSession.copy(accessToken = accessToken)
        storeSession(updatedSession)
    }
}
