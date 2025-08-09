package com.cereal.command.monitor.data.tgtg

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

    @Test
    fun `should create TgtgConfig with default values`() {
        val config = TgtgConfig()

        assertEquals("ANDROID", config.deviceType)
        assertEquals("23.2.1", config.appVersion)
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
                )

            assertNotNull(apiClient)
        }

    @Test
    fun `should create TgtgAppVersionUpdater successfully`() {
        val versionUpdater =
            TgtgAppVersionUpdater(
                logRepository = mockLogRepository,
            )

        assertNotNull(versionUpdater)
    }

    @Test
    fun `should create TgtgExample successfully`() {
        val example =
            TgtgExample(
                logRepository = mockLogRepository,
                preferenceComponent = mockPreferenceComponent,
            )

        assertNotNull(example)
    }
}
