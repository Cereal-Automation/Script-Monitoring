package com.cereal.command.monitor.data.common.httpclient.plugin

import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

class RateLimiterPlugin(
    private val interval: Duration
) {

    private val mutex = Mutex()
    private var lastRequestTime = 0L

    val plugin = createClientPlugin("RateLimiterPlugin") {
        onRequest { request, _ ->
            mutex.withLock {
                val currentTime = System.currentTimeMillis()
                val elapsed = currentTime - lastRequestTime
                if (elapsed < interval.inWholeMilliseconds) {
                    delay(interval.inWholeMilliseconds - elapsed)
                }
                lastRequestTime = System.currentTimeMillis()
            }
        }
    }
}
