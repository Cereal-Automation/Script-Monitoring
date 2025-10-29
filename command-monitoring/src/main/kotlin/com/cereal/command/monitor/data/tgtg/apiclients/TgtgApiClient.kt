package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.tgtg.TgtgConfig
import com.cereal.command.monitor.data.tgtg.TgtgSession
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.DataDomeCookieResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.ListItemsRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.ListItemsResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenResponse
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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
            mutableMapOf(
                HttpHeaders.ContentType to ContentType.Application.Json.toString(),
                HttpHeaders.Accept to "application/json",
                HttpHeaders.AcceptLanguage to "en-US",
                HttpHeaders.AcceptEncoding to "gzip",
                HttpHeaders.UserAgent to "TGTG/$appVersion Dalvik/2.1.0 (Linux; Android 12; SM-G920V Build/MMB29K)",
                "x-correlation-id" to getTgtgConfig().correlationId,
            )

        // If we have a stored DataDome cookie, include it.
        getTgtgConfig().datadomeCookie?.let { cookieValue ->
            headers[HttpHeaders.Cookie] = cookieValue
        }

        return defaultHttpClient(
            timeout = timeout,
            httpProxy = httpProxy,
            defaultHeaders = headers,
            enableRetryPlugin = true,
        )
    }

    // region DataDome
    private suspend fun fetchDataDomeCookie(originalRequestPath: String): String? { // ktlint formatted
        // region datadome fetch

        return try {
            val appVersion = playStoreApiClient.getAppVersion()
            val cid = UUID.randomUUID().toString().replace("-", "").take(64) // pseudo cid generation
            val requestUrlEncoded =
                URLEncoder.encode(
                    "$baseUrl$originalRequestPath",
                    StandardCharsets.UTF_8.toString(),
                )
            val userAgent = "TGTG/$appVersion Dalvik/2.1.0 (Linux; U; Android 14; Pixel 7 Pro Build/UQ1A.240105.004)"
            val timestamp = System.currentTimeMillis()
            val eventsJson =
                "[%7B%22id%22:1,%22message%22:%22response validation%22,%22source%22:%22sdk%22,%22date%22:$timestamp%7D]" // mimic events

            val formParameters = parameters {
                append("cid", cid)
                append("ddk", "1D42C2CA6131C526E09F294FE96F94")
                append("request", requestUrlEncoded)
                append("ua", userAgent)
                append("events", eventsJson)
                append("inte", "android-java-okhttp")
                append("ddv", "3.0.4")
                append("ddvc", appVersion)
                append("os", "Android")
                append("osr", "14")
                append("osn", "UPSIDE_DOWN_CAKE")
                append("osv", "34")
                append("screen_x", "1440")
                append("screen_y", "3120")
                append("screen_d", "3.5")
                append(
                    "camera",
                    "{\"auth\":\"true\", \"info\":\"{\\\"front\\\":\\\"2000x1500\\\",\\\"back\\\":\\\"5472x3648\\\"}\"}",
                )
                append("mdl", "Pixel 7 Pro")
                append("prd", "Pixel 7 Pro")
                append("mnf", "Google")
                append("dev", "cheetah")
                append("hrd", "GS201")
                append("fgp", "google/cheetah/cheetah:14/UQ1A.240105.004/10814564:user/release-keys")
                append("tgs", "release-keys")
                append("d_ifv", UUID.randomUUID().toString().replace("-", ""))
            }

            val httpClient = defaultHttpClient(timeout = timeout, httpProxy = httpProxy)
            val response: HttpResponse = httpClient.post("https://api-sdk.datadome.co/sdk/") {
                headers {
                    append(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                    append(HttpHeaders.UserAgent, "okhttp/5.1.0")
                    append("Connection", "Keep-Alive")
                    append("Host", "api-sdk.datadome.co")
                }
                setBody(formParameters)
            }
            val body = response.bodyAsText()
            val dataDomeResponse = try {
                json.decodeFromString(
                    DataDomeCookieResponse.serializer(),
                    body,
                )
            } catch (e: Exception) {
                null
            }
            val cookieFull = dataDomeResponse?.cookie
            val cookie = cookieFull?.split(';')?.firstOrNull() // take only key=value part
            if (cookie != null) {
                val currentConfig = getTgtgConfig()
                storeTgtgConfig(currentConfig.copy(datadomeCookie = cookie))
                logRepository.debug("Stored DataDome cookie")
            } else {
                logRepository.debug("DataDome cookie not found in response")
            }
            cookie
        } catch (e: Exception) {
            logRepository.debug("Failed to fetch DataDome cookie: ${e.message}")
            null
        }
    }

    private suspend fun <T> executeWith403Retry(
        path: String,
        bodyBuilder: () -> String,
        decode: (String) -> T,
        authHeader: String? = null,
    ): T? {
        val httpClient = createHttpClient()
        val response =
            httpClient.post("${baseUrl}$path") {
                authHeader?.let { headers[HttpHeaders.Authorization] = it }
                setBody(bodyBuilder())
            }
        if (response.status.value == 403) {
            logRepository.debug("Received 403 for $path. Attempting DataDome cookie fetch.")
            val cookie = fetchDataDomeCookie(path)
            if (cookie != null) {
                val retryClient = createHttpClient() // picks up stored cookie
                val retryResponse =
                    retryClient.post("${baseUrl}$path") {
                        authHeader?.let { headers[HttpHeaders.Authorization] = it }
                        setBody(bodyBuilder())
                    }
                val retryBody = retryResponse.bodyAsText()
                return decode(retryBody)
            }
        }
        val bodyText = response.bodyAsText()
        return decode(bodyText)
    }
    // endregion

    suspend fun authByEmail(email: String): AuthByEmailResponse {
        val currentConfig = getTgtgConfig()
        val updatedConfig = currentConfig.copy(correlationId = UUID.randomUUID().toString())
        storeTgtgConfig(updatedConfig)

        val request =
            AuthByEmailRequest(
                deviceType = updatedConfig.deviceType,
                email = email,
            )

        return executeWith403Retry(
            path = "auth/v5/authByEmail",
            bodyBuilder = { json.encodeToString(AuthByEmailRequest.serializer(), request) },
            decode = { body -> json.decodeFromString(AuthByEmailResponse.serializer(), body) },
        ) ?: throw IllegalStateException("AuthByEmail returned null")
    }

    suspend fun authPoll(
        pollingId: String,
        email: String,
    ): AuthPollResponse? {
        val currentConfig = getTgtgConfig()
        val request =
            AuthPollRequest(
                deviceType = currentConfig.deviceType,
                email = email,
                requestPollingId = pollingId,
            )

        val result =
            executeWith403Retry(
                path = "auth/v5/authByRequestPollingId",
                bodyBuilder = { json.encodeToString(AuthPollRequest.serializer(), request) },
                decode = { body -> json.decodeFromString(AuthPollResponse.serializer(), body) },
            )

        if (result == null) return null

        // If status code was 202 we returned null earlier; logic moved inside executeWith403Retry? We still need to handle 202.
        // For this path, if decode succeeded but tokens present create session.
        result.accessToken?.let { accessToken ->
            result.refreshToken?.let { refreshToken ->
                createSession(accessToken, refreshToken)
            }
        }

        return result
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

        val tokenResponse =
            executeWith403Retry(
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

    suspend fun listItems(request: ListItemsRequest): ListItemsResponse? {
        val config = getTgtgConfig()
        val session = config.session
        if (session?.refreshToken == null) {
            logRepository.info("You are not logged in.")
            return null
        }

        val response =
            executeWith403Retry(
                path = "item/v8/",
                bodyBuilder = { json.encodeToString(ListItemsRequest.serializer(), request) },
                decode = { body -> json.decodeFromString(ListItemsResponse.serializer(), body) },
                authHeader = "Bearer ${session.accessToken}",
            )

        return response
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
