package com.cereal.command.monitor.data.common.httpclient

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Simple in-memory cookie storage that can be pre-populated with cookies.
 */
class PrePopulatedCookiesStorage(initialCookies: List<Cookie> = emptyList()) : CookiesStorage {
    private val mutex = Mutex()
    private val cookies = initialCookies.toMutableList()

    override suspend fun get(requestUrl: Url): List<Cookie> =
        mutex.withLock {
            cookies.filter { cookie ->
                // Basic domain/path match; Ktor will do additional filtering.
                (cookie.domain == null || requestUrl.host.endsWith(cookie.domain!!.trimStart('.')))
            }
        }

    override suspend fun addCookie(
        requestUrl: Url,
        cookie: Cookie,
    ) {
        mutex.withLock {
            val idx =
                cookies.indexOfFirst { it.name == cookie.name && it.domain == cookie.domain && it.path == cookie.path }
            if (idx >= 0) {
                cookies[idx] = cookie
            } else {
                cookies.add(cookie)
            }
        }
    }

    override fun close() {}
}
