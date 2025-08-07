package com.cereal.command.monitor.data.tgtg

import com.cereal.command.monitor.data.common.httpclient.defaultHttpClient
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.Proxy
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TgtgAppVersionUpdater(
    private val logRepository: LogRepository,
    private val httpProxy: Proxy? = null,
    private val timeout: Duration = 30.seconds,
) {
    private val googlePlayUrl = "https://play.google.com/store/apps/details?id=com.app.tgtg"

    suspend fun updateAppVersion(config: TgtgConfig): Boolean {
        return try {
            val httpClient = defaultHttpClient(
                timeout = timeout,
                httpProxy = httpProxy,
                logRepository = logRepository
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
                val latestVersion = candidateVersions.maxWithOrNull { a, b ->
                    compareVersions(a, b)
                }

                latestVersion?.let { version ->
                    config.appVersion = version
                    logRepository.info("Updated TGTG app version to: $version")
                    return true
                }
            }

            logRepository.info("Could not find any version information on Google Play page")
            false
        } catch (error: Exception) {
            logRepository.info("Error while retrieving latest version of TGTG on Google Play page: ${error.message}")
            false
        }
    }

    private fun compareVersions(version1: String, version2: String): Int {
        val parts1 = version1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = version2.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxLength) {
            val part1 = parts1.getOrNull(i) ?: 0
            val part2 = parts2.getOrNull(i) ?: 0

            when {
                part1 < part2 -> return -1
                part1 > part2 -> return 1
            }
        }

        return 0
    }
}
