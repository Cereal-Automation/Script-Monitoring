package com.cereal.script.commands.monitor.data.factories

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
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration

object HttpClientFactory {
    fun create(
        timeout: Duration,
        httpProxy: Proxy?,
        defaultHeaders: Map<String, Any> = emptyMap(),
    ): HttpClient =
        HttpClient {
            engine {
                httpProxy?.let {
                    proxy = ProxyBuilder.http("http://${it.address}:${it.port}")
                }
            }
            install(ContentNegotiation) {
                json(
                    JsonFactory.create(),
                )
            }
            install(Logging) {
                level = LogLevel.HEADERS
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
                defaultHeaders.forEach { (key, value) -> header(key, value) }
            }
        }
}
