package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.command.monitor.data.common.httpclient.PrePopulatedCookiesStorage
import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.tgtg.TgtgConfig
import com.cereal.command.monitor.data.tgtg.TgtgSession
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.CaptchaCookieResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.CaptchaUrlHolder
import com.cereal.command.monitor.data.tgtg.apiclients.models.ListItemsRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.ListItemsResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenRequest
import com.cereal.command.monitor.data.tgtg.apiclients.models.RefreshTokenResponse
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TgtgApiClient(
    private val logRepository: LogRepository,
    private val preferenceComponent: PreferenceComponent,
    private val playStoreApiClient: PlayStoreApiClient,
    private val httpProxy: Proxy? = null,
    private val timeout: Duration = 30.seconds,
    private val userInteractionComponent: UserInteractionComponent? = null,
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
        val currentConfig = getTgtgConfig()
        val headers =
            mutableMapOf(
                HttpHeaders.ContentType to ContentType.Application.Json.toString(),
                HttpHeaders.Accept to "application/json",
                HttpHeaders.AcceptLanguage to "en-US",
                HttpHeaders.AcceptEncoding to "gzip",
                HttpHeaders.UserAgent to "TGTG/$appVersion Dalvik/2.1.0 (Linux; Android 12; SM-G920V Build/MMB29K)",
                "x-correlation-id" to currentConfig.correlationId,
            )

        val cookieStorage =
            currentConfig.datadomeCookie?.let { raw ->
                val parts = raw.split('=', limit = 2)
                val name = parts.getOrNull(0) ?: "datadome"
                val value = parts.getOrNull(1) ?: ""
                PrePopulatedCookiesStorage(
                    listOf(
                        Cookie(
                            name = name,
                            value = value,
                            domain = ".apptoogoodtogo.com",
                            path = "/",
                            secure = true,
                            httpOnly = false,
                        ),
                    ),
                )
            } ?: PrePopulatedCookiesStorage()

        return defaultHttpClient(
            timeout = timeout,
            httpProxy = httpProxy,
            defaultHeaders = headers,
            cookieStorage = cookieStorage,
            enableRetryPlugin = true,
        )
    }

    private suspend fun <TgtgResponse : HttpResponse> withCaptchaRetries(
        maxAttempts: Int = 3,
        makeRequest: suspend () -> TgtgResponse,
    ): TgtgResponse {
        var attempt = 0
        var lastError: Exception? = null
        while (attempt < maxAttempts) {
            attempt++
            try {
                val response = makeRequest()
                if (response.status == HttpStatusCode.Forbidden) {
                    val body = response.bodyAsText()
                    val captchaUrl =
                        try {
                            json.decodeFromString(CaptchaUrlHolder.serializer(), body).url
                        } catch (_: Exception) {
                            null
                        }
                    if (captchaUrl != null) {
                        if (userInteractionComponent == null) {
                            throw Exception("Captcha challenge encountered but no UserInteractionComponent available to solve it")
                        }
                        logRepository.info("Encountered captcha challenge. Prompting user to solve.")
                        val solved = solveCaptcha(captchaUrl)
                        if (solved) {
                            logRepository.info("Captcha solved. Retrying request.")
                            continue
                        } else {
                            throw Exception("Failed to solve captcha challenge")
                        }
                    }
                }
                return response
            } catch (e: Exception) {
                lastError = e
                if (attempt >= maxAttempts) break else continue
            }
        }
        throw lastError ?: Exception("Failed after ${'$'}maxAttempts attempts")
    }

    suspend fun authByEmail(email: String): AuthByEmailResponse {
        val response =
            withCaptchaRetries {
                val currentConfig = getTgtgConfig()
                val updatedConfig = currentConfig.copy(correlationId = UUID.randomUUID().toString())
                storeTgtgConfig(updatedConfig)
                val request = AuthByEmailRequest(deviceType = updatedConfig.deviceType, email = email)
                val httpClient = createHttpClient()
                httpClient.post("${baseUrl}auth/v5/authByEmail") {
                    setBody(json.encodeToString(AuthByEmailRequest.serializer(), request))
                }
            }
        val bodyText = response.bodyAsText()
        return json.decodeFromString(AuthByEmailResponse.serializer(), bodyText)
    }

    // Execute the captcha check URL intercepted from the webview.
    private suspend fun executeCaptchaCheck(fullUrl: String): Boolean {
        val httpClient = createHttpClient()
        val response: HttpResponse = httpClient.get(fullUrl)
        if (response.status != HttpStatusCode.OK) {
            logRepository.debug("Captcha check failed with status ${'$'}{response.status}")
            return false
        }
        val bodyText = response.bodyAsText()
        val cookieResponse = json.decodeFromString(CaptchaCookieResponse.serializer(), bodyText)
        val rawCookie = cookieResponse.cookie ?: return false
        // Extract first segment before ';' to use as Cookie header value
        val cookieHeaderValue = rawCookie.substringBefore(';')
        storeDatadomeCookie(cookieHeaderValue)
        logRepository.info("Stored DataDome cookie for subsequent TGTG requests")
        return true
    }

    private suspend fun storeDatadomeCookie(cookie: String) {
        val currentConfig = getTgtgConfig()
        storeTgtgConfig(currentConfig.copy(datadomeCookie = cookie))
    }

    suspend fun authPoll(
        pollingId: String,
        email: String,
    ): AuthPollResponse? {
        val response =
            withCaptchaRetries {
                val currentConfig = getTgtgConfig()
                val request =
                    AuthPollRequest(
                        deviceType = currentConfig.deviceType,
                        email = email,
                        requestPollingId = pollingId,
                    )
                val httpClient = createHttpClient()
                httpClient.post("${baseUrl}auth/v5/authByRequestPollingId") {
                    setBody(json.encodeToString(AuthPollRequest.serializer(), request))
                }
            }
        if (response.status.value == 202) return null
        val bodyText = response.bodyAsText()
        val authResponse = json.decodeFromString(AuthPollResponse.serializer(), bodyText)
        authResponse.accessToken?.let { at ->
            authResponse.refreshToken?.let { rt ->
                createSession(at, rt)
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
        val response =
            withCaptchaRetries {
                val request = RefreshTokenRequest(refreshToken = refreshToken)
                val httpClient = createHttpClient()
                httpClient.post("${baseUrl}token/v1/refresh") {
                    setBody(json.encodeToString(RefreshTokenRequest.serializer(), request))
                }
            }
        if (!response.status.isSuccess()) return false
        val bodyText = response.bodyAsText()
        val tokenResponse = json.decodeFromString(RefreshTokenResponse.serializer(), bodyText)
        tokenResponse.accessToken?.let { accessToken ->
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
            withCaptchaRetries {
                val httpClient = createHttpClient()
                httpClient.post("${baseUrl}item/v8/") {
                    headers[HttpHeaders.Authorization] = "Bearer ${session.accessToken}"
                    setBody(json.encodeToString(ListItemsRequest.serializer(), request))
                }
            }
        val bodyText = response.bodyAsText()
        return json.decodeFromString(ListItemsResponse.serializer(), bodyText)
    }

    private suspend fun solveCaptcha(challengeUrl: String): Boolean {
        val ui = userInteractionComponent ?: return false
        val result =
            ui.showUrl(
                title = "Solve TGTG Captcha",
                url = challengeUrl,
            ) {
                it.url.contains("/captcha/check")
            }
        return executeCaptchaCheck(result.url)
    }

    private suspend fun createSession(
        accessToken: String,
        refreshToken: String,
    ) {
        val currentConfig = getTgtgConfig()
        val session = TgtgSession(accessToken = accessToken, refreshToken = refreshToken)
        val updatedConfig = currentConfig.copy(session = session)
        storeTgtgConfig(updatedConfig)
    }

    private suspend fun updateSession(accessToken: String) {
        val currentConfig = getTgtgConfig()
        currentConfig.session?.let { currentSession ->
            val updatedSession = currentSession.copy(accessToken = accessToken)
            val updatedConfig = currentConfig.copy(session = updatedSession)
            storeTgtgConfig(updatedConfig)
        }
    }
}
