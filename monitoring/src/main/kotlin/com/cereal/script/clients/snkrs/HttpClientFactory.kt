package com.cereal.script.clients.snkrs

import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import one.ifelse.tools.useragent.RandomUserAgent
import kotlin.time.Duration

object HttpClientFactory {
    fun create(
        timeout: Duration,
        httpProxy: Proxy?,
    ): HttpClient =
        HttpClient {
            engine {
                httpProxy?.let {
                    // TODO
                    proxy = ProxyBuilder.http("http://TODO:TODO")
                }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    },
                )
            }
            install(Logging) {
                level = LogLevel.BODY
            }
            install(HttpTimeout) {
                requestTimeoutMillis = timeout.inWholeMilliseconds
            }
            install(ContentEncoding) {
                gzip()
                deflate()
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = httpProxy?.username.orEmpty(),
                            password = httpProxy?.password.orEmpty(),
                        )
                    }
                }
            }
            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
                header(HttpHeaders.AcceptLanguage, "en-GB,en;q=0.9")
                header("appid", "com.nike.commerce.snkrs.web")
                header("DNT", "1")
                header("nike-api-caller-id", "nike:snkrs:web:1.0")
                header(HttpHeaders.Origin, "https://www.nike.com")
                header(HttpHeaders.Referrer, "https://www.nike.com/")
                header("Sec-Fetch-Dest", "empty")
                header("Sec-Fetch-Mode", "cors")
                header("Sec-Fetch-Site", "same-site")
                header(
                    HttpHeaders.UserAgent,
                    RandomUserAgent.random({ it.deviceCategory == "mobile" && it.userAgent.contains("Chrome") }),
                )
                header(HttpHeaders.CacheControl, "no-cache, no-store, must-revalidate")
                header(HttpHeaders.Pragma, "no-cache")
                header(HttpHeaders.Expires, "0")
            }
        }
}
