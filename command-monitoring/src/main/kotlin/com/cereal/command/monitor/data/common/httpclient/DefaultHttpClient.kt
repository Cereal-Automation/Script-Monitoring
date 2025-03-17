package com.cereal.command.monitor.data.common.httpclient

import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.http
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

fun defaultHttpClient(
    timeout: Duration,
    httpProxy: Proxy?,
    logRepository: LogRepository,
    defaultHeaders: Map<String, Any> = emptyMap(),
): HttpClient =
    HttpClient(CIO) {
        engine {
            httpProxy?.let {
                proxy = ProxyBuilder.http("http://${it.address}:${it.port}")
            }
        }
        install(ContentNegotiation) {
            json(
                defaultJson(),
            )
        }
        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) {
                        runBlocking {
                            logRepository.debug(message)
                        }
                    }
                }
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
