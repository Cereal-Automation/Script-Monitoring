package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.script.repository.LogRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders

/**
 * Executes POST requests to the TGTG API with support for a 403 -> DataDome cookie acquisition retry.
 */
internal class HttpExecutor(
    private val baseUrl: String,
    private val logRepository: LogRepository,
    private val cookieManager: DataDomeCookieManager,
    private val httpClientFactory: suspend () -> HttpClient,
) {
    suspend fun <T> postWith403Retry(
        path: String,
        authHeader: String? = null,
        bodyBuilder: () -> String,
        decode: (String) -> T,
    ): T? {
        val client = httpClientFactory()
        val response =
            client.post("${baseUrl}$path") {
                authHeader?.let { headers[HttpHeaders.Authorization] = it }
                setBody(bodyBuilder())
            }
        if (response.status.value == 403) {
            logRepository.debug("Received 403 for $path. Attempting DataDome cookie fetch.")
            val cookie = cookieManager.fetch(path)
            if (cookie != null) {
                val retryClient = httpClientFactory() // picks up stored cookie via new storage
                val retryResponse =
                    retryClient.post("${baseUrl}$path") {
                        authHeader?.let { headers[HttpHeaders.Authorization] = it }
                        setBody(bodyBuilder())
                    }
                return decode(retryResponse.bodyAsText())
            }
        }
        return decode(response.bodyAsText())
    }
}
