package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.command.monitor.data.common.cache.CacheManager
import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.command.monitor.data.tgtg.apiclients.exception.TgtgAppVersionException
import com.cereal.script.repository.LogRepository
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class PlayStoreApiClient(
    private val logRepository: LogRepository,
    private val cacheManager: CacheManager,
    private val timeout: Duration = 30.seconds,
) {
    private val googlePlayUrl = "https://play.google.com/store/apps/details?id=com.app.tgtg"

    suspend fun getAppVersion(): String {
        // Check cache first
        val cachedVersion = cacheManager.retrieve(CACHE_KEY)

        if (cachedVersion != null) {
            return cachedVersion
        }

        // Cache miss or expired, fetch from Google Play
        try {
            val httpClient =
                defaultHttpClient(
                    timeout = timeout,
                    httpProxy = null,
                )

            val response = httpClient.get(googlePlayUrl)
            val htmlContent = response.bodyAsText()

            val candidateVersions = mutableListOf<String>()

            // Find all AF_initDataCallback scripts
            val initDataCallbackRegex =
                """<script class="[^"]*" nonce="[^"]*">AF_initDataCallback\((.*?)\);</script>""".toRegex()
            val initDataMatches = initDataCallbackRegex.findAll(htmlContent)

            for (match in initDataMatches) {
                val scriptContent = match.groupValues[1]

                // Find version patterns within the script content
                val versionRegex = """(\d+\.\d+\.\d+)""".toRegex()
                val versionMatches = versionRegex.findAll(scriptContent)

                for (versionMatch in versionMatches) {
                    candidateVersions.add(versionMatch.groupValues[1])
                }
            }

            if (candidateVersions.isNotEmpty()) {
                // Find the highest version number
                val latestVersion =
                    candidateVersions.maxWithOrNull { a, b ->
                        a.compareVersions(b)
                    }

                latestVersion?.let { version ->
                    logRepository.info("Found TGTG app version: $version")
                    cacheManager.store(CACHE_KEY, version, 24.hours)
                    return version
                }
            }

            logRepository.info("Could not find any version information on Google Play page")
            throw TgtgAppVersionException("Could not find any version information on Google Play page")
        } catch (error: Exception) {
            logRepository.info("Error while retrieving latest version of TGTG on Google Play page: ${error.message}")
            throw TgtgAppVersionException(
                "Error while retrieving latest version of TGTG on Google Play page: ${error.message}",
                error,
            )
        }
    }

    companion object {
        private const val CACHE_KEY = "tgtg_app_version"
    }
}
