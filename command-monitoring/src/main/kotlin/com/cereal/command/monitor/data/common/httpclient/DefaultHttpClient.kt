package com.cereal.command.monitor.data.common.httpclient

import com.cereal.command.monitor.data.common.json.defaultJson
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import okhttp3.Authenticator
import okhttp3.Credentials
import java.net.InetSocketAddress
import kotlin.time.Duration

fun defaultHttpClient(
    timeout: Duration,
    httpProxy: Proxy?,
    defaultHeaders: Map<String, Any> = emptyMap(),
    cookieStorage: CookiesStorage = AcceptAllCookiesStorage(),
    enableRetryPlugin: Boolean = false,
): HttpClient =
    HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)

                httpProxy?.let { cerealProxy ->
                    val proxy =
                        java.net.Proxy(
                            java.net.Proxy.Type.HTTP,
                            InetSocketAddress(cerealProxy.address, cerealProxy.port),
                        )
                    proxy(proxy)

                    cerealProxy.username?.let { username ->
                        val proxyAuthenticator =
                            Authenticator { _, response ->
                                val credential = Credentials.basic(username, cerealProxy.password.orEmpty())
                                response.request
                                    .newBuilder()
                                    .header("Proxy-Authorization", credential)
                                    .build()
                            }

                        proxyAuthenticator(proxyAuthenticator)
                    }
                }
            }
        }
        install(ContentNegotiation) {
            json(
                defaultJson(),
            )
        }
        install(HttpCookies) {
            storage = cookieStorage
        }
        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            requestTimeoutMillis = timeout.inWholeMilliseconds
        }
        install(ContentEncoding) {
            gzip()
            deflate()
        }
        if (enableRetryPlugin) {
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 2)
                retryIf(maxRetries = 2) { request, response ->
                    // Retry 403 requests because on first request cookies might get set.
                    response.status.value == 403
                }
            }
        }
        defaultRequest {
            defaultHeaders.forEach { (key, value) -> header(key, value) }
        }
    }
