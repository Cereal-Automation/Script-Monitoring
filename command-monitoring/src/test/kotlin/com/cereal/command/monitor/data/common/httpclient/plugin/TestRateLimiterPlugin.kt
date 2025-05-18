package com.cereal.command.monitor.data.common.httpclient.plugin

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class TestRateLimiterPlugin {
    @Test
    fun `test rate limiting with single request`() =
        runTest {
            val interval = 100.milliseconds
            val plugin = RateLimiterPlugin(interval).plugin
            val client =
                HttpClient(MockEngine) {
                    install(plugin)
                    engine {
                        addHandler { request ->
                            respondOk()
                        }
                    }
                }

            val startTime = currentTime
            client.get("https://example.com")
            val endTime = currentTime

            assertEquals(0L, endTime - startTime, "No delay expected for the first request")
        }

    @Test
    fun `test rate limiting with multiple requests`() =
        runTest {
            val interval = 100.milliseconds
            val plugin = RateLimiterPlugin(interval).plugin
            val client =
                HttpClient(MockEngine) {
                    install(plugin)
                    engine {
                        addHandler { request ->
                            respondOk()
                        }
                    }
                }

            // Make an initial request to start the rate limiter
            client.get("https://example.com/initial")

            val startTime = currentTime
            client.get("https://example.com/1")
            client.get("https://example.com/2")
            val endTime = currentTime

            assertTrue(
                (endTime - startTime) >= interval.inWholeMilliseconds * 0.9,
                "Delay should match the interval. Expected at least ${interval.inWholeMilliseconds * 0.9}ms but got ${endTime - startTime}ms"
            )
        }

    @Test
    fun `test rate limiting with different request methods`() =
        runTest {
            val interval = 100.milliseconds
            val plugin = RateLimiterPlugin(interval).plugin
            val client =
                HttpClient(MockEngine) {
                    install(plugin)
                    engine {
                        addHandler { request ->
                            respondOk()
                        }
                    }
                }

            // Make an initial request to start the rate limiter
            client.get("https://example.com/initial")

            val startTime = currentTime
            client.get("https://example.com/1")
            client.post("https://example.com/2") { }
            val endTime = currentTime

            assertTrue(
                (endTime - startTime) >= interval.inWholeMilliseconds * 0.9,
                "Delay should match the interval regardless of method. Expected at least ${interval.inWholeMilliseconds * 0.9}ms but got ${endTime - startTime}ms"
            )
        }

    @Test
    fun `test rate limiting with longer interval`() =
        runTest {
            val interval = 500.milliseconds
            val plugin = RateLimiterPlugin(interval).plugin
            val client =
                HttpClient(MockEngine) {
                    install(plugin)
                    engine {
                        addHandler { request ->
                            respondOk()
                        }
                    }
                }

            // Make an initial request to start the rate limiter
            client.get("https://example.com/initial")

            // Now measure the time for the next requests
            val startTime = currentTime
            client.get("https://example.com/1")
            client.get("https://example.com/2")
            val endTime = currentTime

            assertTrue(
                (endTime - startTime) >= interval.inWholeMilliseconds * 0.9,
                "Delay should match the longer interval. Expected at least ${interval.inWholeMilliseconds * 0.9}ms but got ${endTime - startTime}ms",
            )
        }

    @Test
    fun `test rate limiting with zero interval`() =
        runTest {
            val interval = 0.milliseconds
            val plugin = RateLimiterPlugin(interval).plugin
            val client =
                HttpClient(MockEngine) {
                    install(plugin)
                    engine {
                        addHandler { request ->
                            respondOk()
                        }
                    }
                }

            val startTime = currentTime
            client.get("https://example.com/1")
            client.get("https://example.com/2")
            val endTime = currentTime

            assertEquals(0L, endTime - startTime, "No delay expected with zero interval")
        }

    @Test
    fun `test rate limiting with real HTTP client (integration test)`() =
        runTest {
            // Note: This is more of an integration test and might be slower.
            // It uses a real HTTP client and a mock server to verify the plugin's behavior.
            val interval = 200.milliseconds
            val plugin = RateLimiterPlugin(interval).plugin

            val mockEngine =
                MockEngine { request ->
                    respondOk(
                        content = "OK",
                    )
                }

            val client =
                HttpClient(mockEngine) {
                    install(plugin)
                }

            // Make an initial request to start the rate limiter
            client.get("https://example.com/initial")

            val startTime = currentTime
            client.get("https://example.com/1")
            client.get("https://example.com/2")
            val endTime = currentTime

            assertTrue(
                (endTime - startTime) >= interval.inWholeMilliseconds * 0.9,
                "Delay should match the interval (integration test). Expected at least ${interval.inWholeMilliseconds * 0.9}ms but got ${endTime - startTime}ms"
            )
        }
}