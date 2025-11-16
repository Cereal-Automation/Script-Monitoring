package com.cereal.command.monitor.data.bolcom.httpclient

import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Credentials
import java.net.InetSocketAddress
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun defaultBolComHttpClient(
    logRepository: LogRepository,
    httpProxy: Proxy? = null,
    defaultHeaders: Map<String, Any> = emptyMap(),
    cookieStorage: CookiesStorage = AcceptAllCookiesStorage(),
    timeout: Duration = 300.seconds,
): HttpClient =
    HttpClient(OkHttp) {
        expectSuccess = true
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
        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) {
                        runBlocking {
                            logRepository.debug(message)
                        }
                    }
                }
            level = LogLevel.NONE
        }
        install(HttpTimeout) {
            requestTimeoutMillis = timeout.inWholeMilliseconds
            socketTimeoutMillis = timeout.inWholeMilliseconds
        }
        install(HttpCookies) {
            storage = cookieStorage
        }
        install(ContentEncoding) {
            gzip()
            deflate()
        }
        defaultRequest {
            defaultHeaders.forEach { (key, value) -> header(key, value) }
        }
    }
