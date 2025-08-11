package com.cereal.command.monitor.data.tgtg

import com.cereal.command.monitor.data.common.cache.PreferenceCacheManager
import com.cereal.command.monitor.data.tgtg.apiclients.PlayStoreApiClient
import com.cereal.command.monitor.data.tgtg.apiclients.TgtgApiClient
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TgtgApiClientTest {
    private val mockLogRepository = mockk<LogRepository>(relaxed = true)
    private val mockPreferenceComponent = mockk<PreferenceComponent>(relaxed = true)
    private val mockPlayStoreApiClient = mockk<PlayStoreApiClient>(relaxed = true)

    @Test
    fun `should create TgtgConfig with default values`() {
        val config = TgtgConfig()

        assertEquals("ANDROID", config.deviceType)
        assertNotNull(config.correlationId)
    }

    @Test
    fun `should create TgtgApiClient successfully`() =
        runTest {
            val config = TgtgConfig()
            val apiClient =
                TgtgApiClient(
                    logRepository = mockLogRepository,
                    config = config,
                    preferenceComponent = mockPreferenceComponent,
                    playStoreApiClient = mockPlayStoreApiClient,
                )

            assertNotNull(apiClient)
        }

    @Test
    fun `should create TgtgAppVersionDataSource successfully`() {
        val cacheManager = PreferenceCacheManager(mockPreferenceComponent)
        val versionDataSource =
            PlayStoreApiClient(
                logRepository = mockLogRepository,
                cacheManager = cacheManager,
            )

        assertNotNull(versionDataSource)
    }

}
