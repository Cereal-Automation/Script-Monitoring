package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.command.monitor.data.tgtg.TgtgConfig
import com.cereal.command.monitor.data.tgtg.apiclients.models.DataDomeCookieResponse
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.time.Duration

/**
 * Handles DataDome cookie initial population & persistence.
 */
internal class DataDomeCookieManager(
    private val baseUrl: String,
    private val timeout: Duration,
    private val httpProxy: Proxy?,
    private val logRepository: LogRepository,
    private val playStoreApiClient: PlayStoreApiClient,
    private val configStore: TgtgConfigStore,
    private val json: Json = defaultJson(),
) {
    companion object {
        private const val DATA_DOME_COOKIE_NAME = "datadome"
    }

    suspend fun createCookieStorage(initialConfig: TgtgConfig): CookiesStorage {
        val baseStorage = AcceptAllCookiesStorage()
        val tgtgUrl = Url(baseUrl)

        initialConfig.datadomeCookie?.let { cookieValue ->
            val parts = cookieValue.split('=', limit = 2)
            if (parts.size == 2) {
                val (name, value) = parts.map { it.trim() }
                if (name.isNotEmpty() && value.isNotEmpty()) {
                    baseStorage.addCookie(
                        tgtgUrl,
                        io.ktor.http.Cookie(name = name, value = value, domain = tgtgUrl.host, path = "/"),
                    )
                }
            }
        }

        return object : CookiesStorage by baseStorage {
            override suspend fun addCookie(
                requestUrl: Url,
                cookie: io.ktor.http.Cookie,
            ) {
                baseStorage.addCookie(requestUrl, cookie)
                if (cookie.name.equals(DATA_DOME_COOKIE_NAME, ignoreCase = true)) {
                    val trimmedValue = cookie.value.trim().takeIf { it.isNotEmpty() }
                    val cookieString = trimmedValue?.let { "${cookie.name}=$it" }
                    configStore.update { it.copy(datadomeCookie = cookieString) }
                }
            }
        }
    }

    /** Fetches a new DataDome cookie with a synthetic SDK request and persists it. */
    suspend fun fetch(originalRequestPath: String): String? =
        try {
            val appVersion = playStoreApiClient.getAppVersion()
            val cid = UUID.randomUUID().toString().replace("-", "").take(64)
            val requestUrlEncoded = URLEncoder.encode("$baseUrl$originalRequestPath", StandardCharsets.UTF_8.toString())
            val userAgent = "TGTG/$appVersion Dalvik/2.1.0 (Linux; U; Android 14; Pixel 7 Pro Build/UQ1A.240105.004)"
            val timestamp = System.currentTimeMillis()
            val eventsJson =
                "[%7B%22id%22:1,%22message%22:%22response validation%22,%22source%22:%22sdk%22,%22date%22:$timestamp%7D]"

            val formParameters =
                parameters {
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
            val response =
                httpClient.post("https://api-sdk.datadome.co/sdk/") {
                    headers {
                        append(HttpHeaders.UserAgent, "okhttp/5.1.0")
                    }
                    setBody(FormDataContent(formParameters))
                }
            val body = response.bodyAsText()
            val dataDomeResponse =
                runCatching { json.decodeFromString(DataDomeCookieResponse.serializer(), body) }.getOrNull()
            val cookieFull = dataDomeResponse?.cookie
            val cookie = cookieFull?.split(';')?.firstOrNull()
            if (cookie != null) {
                configStore.update { it.copy(datadomeCookie = cookie) }
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
