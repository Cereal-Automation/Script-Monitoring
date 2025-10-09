package com.cereal.command.monitor.data.common.httpclient

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultHttpClientTest {
    @Test
    fun `should retry on 403 responses`() =
        runTest {
            var requestCount = 0

            val mockEngine =
                MockEngine { request ->
                    requestCount++
                    when (requestCount) {
                        1, 2, 3 ->
                            respond(
                                content = "Forbidden",
                                status = HttpStatusCode.Forbidden,
                                headers = headersOf("Content-Type", "text/plain"),
                            )
                        else ->
                            respond(
                                content = "Success",
                                status = HttpStatusCode.OK,
                                headers = headersOf("Content-Type", "text/plain"),
                            )
                    }
                }

            // Create client with mock engine and retry plugin
            val client =
                HttpClient(mockEngine) {
                    install(HttpRequestRetry) {
                        retryOnServerErrors(maxRetries = 3)
                        retryIf(maxRetries = 3) { request, response ->
                            response.status.value == 403
                        }
                        exponentialDelay()
                    }
                }

            val response = client.get("https://test.com/api")

            // Should have made 4 requests (3 retries + 1 success)
            assertEquals(4, requestCount)
            assertEquals(HttpStatusCode.OK, response.status)

            client.close()
        }

    @Test
    fun `should not retry on non-403 responses`() =
        runTest {
            var requestCount = 0

            val mockEngine =
                MockEngine { request ->
                    requestCount++
                    respond(
                        content = "Bad Request",
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf("Content-Type", "text/plain"),
                    )
                }

            // Create client with mock engine and retry plugin
            val client =
                HttpClient(mockEngine) {
                    install(HttpRequestRetry) {
                        retryOnServerErrors(maxRetries = 3)
                        retryIf(maxRetries = 3) { request, response ->
                            response.status.value == 403
                        }
                        exponentialDelay()
                    }
                }

            val response = client.get("https://test.com/api")

            // Should have made only 1 request (no retries for 400)
            assertEquals(1, requestCount)
            assertEquals(HttpStatusCode.BadRequest, response.status)

            client.close()
        }
}
