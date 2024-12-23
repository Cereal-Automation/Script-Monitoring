package com.cereal.script.clients.toogoodtogo

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Duration

@Serializable
data class Credentials(
    val email: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val datadomeCookie: String?,
)

@Serializable
data class Item(
    val id: String,
    val name: String,
    val description: String?,
)

@Serializable
data class ApiResponse<T>(
    val state: String?,
    val data: T?,
)

class TgtgClient(
    private val baseUrl: String = "https://apptoogoodtogo.com/api/",
    private val email: String? = null,
    private var accessToken: String? = null,
    private var refreshToken: String? = null,
    private var datadomeCookie: String? = null,
    private val language: String = "en-GB",
    private val timeout: Duration = Duration.ofSeconds(30),
    private val accessTokenLifetime: Long = 4 * 3600,
    private val pollingWaitTime: Long = 5,
    private val maxPollingTries: Int = 24,
    private val deviceType: String = "ANDROID",
) {
    private val client: HttpClient
    private var lastTokenRefreshTime: Long? = null
    private var captchaErrorCount: Int = 0

    init {
        client =
            HttpClient {
                install(ContentNegotiation) {
                    json()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = timeout.toMillis()
                }
                install(Logging) {
                    level = LogLevel.BODY
                }
                defaultRequest {
                    header(HttpHeaders.AcceptLanguage, language)
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    accessToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                }
            }
    }

    suspend fun getCredentials(): Credentials {
        login()
        return Credentials(email, accessToken, refreshToken, datadomeCookie)
    }

    suspend fun login() {
        if (email.isNullOrBlank() && (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank())) {
            throw IllegalArgumentException("You must provide at least email or access_token and refresh_token")
        }
        if (isAlreadyLogged()) {
            refreshTokenIfNecessary()
        } else {
            val response: HttpResponse =
                client.post("${baseUrl}auth/v4/authByEmail") {
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("device_type" to deviceType, "email" to email))
                }
            val jsonResponse = Json.decodeFromString<Map<String, Any>>(response.bodyAsText())
            when (jsonResponse["state"]) {
                "WAIT" -> startPolling(jsonResponse["polling_id"].toString())
                else -> throw Exception("Login failed: ${response.status}")
            }
        }
    }

    private fun isAlreadyLogged(): Boolean = accessToken != null && refreshToken != null

    private fun isTokenExpired(): Boolean {
        val currentTime = System.currentTimeMillis() / 1000
        return lastTokenRefreshTime == null || currentTime - lastTokenRefreshTime!! > accessTokenLifetime
    }

    private suspend fun refreshTokenIfNecessary() {
        if (!isTokenExpired()) return
        val response: HttpResponse =
            client.post("${baseUrl}auth/v4/token/refresh") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refresh_token" to refreshToken))
            }
        val jsonResponse = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
        accessToken = jsonResponse["access_token"]
        refreshToken = jsonResponse["refresh_token"]
        lastTokenRefreshTime = System.currentTimeMillis() / 1000
    }

    private suspend fun startPolling(pollingId: String) {
        repeat(maxPollingTries) {
            val response: HttpResponse =
                client.post("${baseUrl}auth/v4/authByRequestPollingId") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        mapOf(
                            "device_type" to deviceType,
                            "email" to email,
                            "request_polling_id" to pollingId,
                        ),
                    )
                }
            if (response.status == HttpStatusCode.OK) {
                val loginResponse = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
                accessToken = loginResponse["access_token"]
                refreshToken = loginResponse["refresh_token"]
                lastTokenRefreshTime = System.currentTimeMillis() / 1000
                return
            }
            delay(pollingWaitTime * 1000)
        }
        throw Exception("Max polling retries reached. Try again.")
    }

    suspend fun getItems(
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        radius: Int = 21,
        pageSize: Int = 20,
        page: Int = 1,
        favoritesOnly: Boolean = true,
    ): List<Item> {
        login()
        val response: HttpResponse =
            client.post("${baseUrl}item/v8/") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "origin" to mapOf("latitude" to latitude, "longitude" to longitude),
                        "radius" to radius,
                        "page_size" to pageSize,
                        "page" to page,
                        "favorites_only" to favoritesOnly,
                    ),
                )
            }
        return Json.decodeFromString(response.bodyAsText())
    }

    suspend fun getItem(itemId: String): Item {
        login()
        val response: HttpResponse =
            client.post("${baseUrl}item/v8/$itemId") {
                contentType(ContentType.Application.Json)
            }
        return Json.decodeFromString(response.bodyAsText())
    }

    suspend fun getFavorites(): List<Item> {
        login()
        val items = mutableListOf<Item>()
        var page = 1
        val pageSize = 100
        while (true) {
            val newItems = getItems(page = page, pageSize = pageSize, favoritesOnly = true)
            items.addAll(newItems)
            if (newItems.size < pageSize) break
            page++
        }
        return items
    }

    suspend fun setFavorite(
        itemId: String,
        isFavorite: Boolean,
    ) {
        login()
        client.post("${baseUrl}user/favorite/v1/$itemId/update") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("is_favorite" to isFavorite))
        }
    }

    suspend fun createOrder(
        itemId: String,
        itemCount: Int,
    ): Map<String, String> {
        login()
        val response: HttpResponse =
            client.post("${baseUrl}order/v7/create/$itemId") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("item_count" to itemCount))
            }
        val jsonResponse = Json.decodeFromString<Map<String, Any>>(response.bodyAsText())
        if (jsonResponse["state"] != "SUCCESS") {
            throw Exception("Order creation failed")
        }
        return jsonResponse["order"] as Map<String, String>
    }

    suspend fun abortOrder(orderId: String) {
        login()
        val response: HttpResponse =
            client.post("${baseUrl}order/v7/$orderId/abort") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("cancel_reason_id" to 1))
            }
        val jsonResponse = Json.decodeFromString<Map<String, Any>>(response.bodyAsText())
        if (jsonResponse["state"] != "SUCCESS") {
            throw Exception("Order abortion failed")
        }
    }
}
