package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.tgtg.TgtgConfig
import com.cereal.command.monitor.data.tgtg.TgtgSession
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.ListItemsRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.ListItemsResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenResponse
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.CookiesStorage
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
    private val baseUrl = "https://api.toogoodtogo.com/api/"
    private val json = defaultJson()

    private val configStore by lazy { TgtgConfigStore(preferenceComponent, json, logRepository) }
    private val dataDomeManager by lazy {
        DataDomeCookieManager(
            baseUrl,
            timeout,
            httpProxy,
            logRepository,
            playStoreApiClient,
            configStore,
            json,
        )
    }
    private val httpExecutor by lazy { HttpExecutor(baseUrl, logRepository, dataDomeManager) { createHttpClient() } }

    private suspend fun getConfig(): TgtgConfig = configStore.get()

    private suspend fun updateConfig(transform: (TgtgConfig) -> TgtgConfig) = configStore.update(transform)

    private suspend fun createHttpClient(): HttpClient {
        val appVersion = playStoreApiClient.getAppVersion()
        val config = getConfig()
        val headers =
            mutableMapOf(
                HttpHeaders.ContentType to ContentType.Application.Json.toString(),
                HttpHeaders.Accept to "application/json",
                HttpHeaders.AcceptLanguage to "en-US",
                HttpHeaders.AcceptEncoding to "gzip",
                HttpHeaders.UserAgent to "TGTG/$appVersion Dalvik/2.1.0 (Linux; Android 12; SM-G920V Build/MMB29K)",
                "x-correlation-id" to config.correlationId,
            )
        val cookieStorage: CookiesStorage = dataDomeManager.createCookieStorage(config)
        return defaultHttpClient(
            timeout = timeout,
            httpProxy = httpProxy,
            defaultHeaders = headers,
            cookieStorage = cookieStorage,
            enableRetryPlugin = false,
        )
    }

    suspend fun authByEmail(email: String): AuthByEmailResponse {
        val updatedConfig = updateConfig { it.copy(correlationId = UUID.randomUUID().toString()) }
        val request = AuthByEmailRequest(deviceType = updatedConfig.deviceType, email = email)
        return httpExecutor.postWith403Retry(
            path = "auth/v5/authByEmail",
            bodyBuilder = { json.encodeToString(AuthByEmailRequest.serializer(), request) },
            decode = { body -> json.decodeFromString(AuthByEmailResponse.serializer(), body) },
        ) ?: error("AuthByEmail returned null")
    }

    suspend fun authPoll(
        pollingId: String,
        email: String,
    ): AuthPollResponse? {
        val currentConfig = getConfig()
        val request =
            AuthPollRequest(deviceType = currentConfig.deviceType, email = email, requestPollingId = pollingId)
        val result =
            httpExecutor.postWith403Retry(
                path = "auth/v5/authByRequestPollingId",
                bodyBuilder = { json.encodeToString(AuthPollRequest.serializer(), request) },
                decode = { body -> json.decodeFromString(AuthPollResponse.serializer(), body) },
            )
        // Create session if tokens present
        result?.accessToken?.let { at -> result.refreshToken?.let { rt -> createSession(at, rt) } }
        return result
    }

    suspend fun login(): Boolean {
        updateConfig { it.copy(correlationId = UUID.randomUUID().toString()) }
        val session = getConfig().session
        return if (session?.refreshToken != null) {
            refreshToken()
        } else {
            logRepository.info("No refresh token available. Please authenticate first.")
            false
        }
    }

    suspend fun listItems(request: ListItemsRequest): ListItemsResponse? {
        val session = getConfig().session
        if (session?.refreshToken == null) {
            logRepository.info("You are not logged in.")
            return null
        }
        return httpExecutor.postWith403Retry(
            path = "item/v8/",
            authHeader = "Bearer ${session.accessToken}",
            bodyBuilder = { json.encodeToString(ListItemsRequest.serializer(), request) },
            decode = { body -> json.decodeFromString(ListItemsResponse.serializer(), body) },
        )
    }

    private suspend fun refreshToken(): Boolean {
        val refreshToken = getConfig().session?.refreshToken ?: return false
        val request = RefreshTokenRequest(refreshToken = refreshToken)
        val tokenResponse =
            httpExecutor.postWith403Retry(
                path = "token/v1/refresh",
                bodyBuilder = { json.encodeToString(RefreshTokenRequest.serializer(), request) },
                decode = { body -> json.decodeFromString(RefreshTokenResponse.serializer(), body) },
            )
        tokenResponse?.accessToken?.let { accessToken ->
            updateSession(accessToken)
            return true
        }
        return false
    }

    private suspend fun createSession(
        accessToken: String,
        refreshToken: String,
    ) {
        updateConfig { current ->
            current.copy(
                session =
                    TgtgSession(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                    ),
            )
        }
    }

    private suspend fun updateSession(accessToken: String) {
        updateConfig { current ->
            val session = current.session ?: return@updateConfig current
            current.copy(session = session.copy(accessToken = accessToken))
        }
    }
}
