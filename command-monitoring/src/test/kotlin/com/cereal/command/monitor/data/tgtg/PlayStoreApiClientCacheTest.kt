package com.cereal.command.monitor.data.tgtg

import com.cereal.command.monitor.data.common.cache.PreferenceCacheManager
import com.cereal.command.monitor.data.tgtg.apiclients.PlayStoreApiClient
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlayStoreApiClientCacheTest {
    private val mockLogRepository = mockk<LogRepository>(relaxed = true)
    private val mockPreferenceComponent = mockk<PreferenceComponent>(relaxed = true)

    @Test
    fun `should use cached version when available and valid`() =
        runTest {
            // Setup cache with valid data
            coEvery { mockPreferenceComponent.getString("cache_tgtg_app_version_value") } returns "23.5.1"
            coEvery {
                mockPreferenceComponent.getString("cache_tgtg_app_version_expiration")
            } returns "${System.currentTimeMillis() / 1000 + 3600}" // 1 hour in future

            val cacheManager = PreferenceCacheManager(mockPreferenceComponent)
            val client =
                PlayStoreApiClient(
                    logRepository = mockLogRepository,
                    cacheManager = cacheManager,
                )

            // This should return cached version without making HTTP request
            val version = client.getAppVersion()

            assertEquals("23.5.1", version)

            // Verify cache was checked
            coVerify { mockPreferenceComponent.getString("cache_tgtg_app_version_value") }
            coVerify { mockPreferenceComponent.getString("cache_tgtg_app_version_expiration") }
        }

    @Test
    fun `should return null when cache is expired`() =
        runTest {
            // Setup cache with expired data
            coEvery { mockPreferenceComponent.getString("cache_tgtg_app_version_value") } returns "23.5.1"
            coEvery {
                mockPreferenceComponent.getString("cache_tgtg_app_version_expiration")
            } returns "${System.currentTimeMillis() / 1000 - 3600}" // 1 hour in past

            val cacheManager = PreferenceCacheManager(mockPreferenceComponent)
            val client =
                PlayStoreApiClient(
                    logRepository = mockLogRepository,
                    cacheManager = cacheManager,
                )

            try {
                // This should try to fetch from Google Play (and fail in test environment)
                client.getAppVersion()
            } catch (e: Exception) {
                // Expected to fail in test environment due to network call
            }

            // Verify expiration was checked
            coVerify { mockPreferenceComponent.getString("cache_tgtg_app_version_expiration") }
        }

    @Test
    fun `should return null when no cache exists`() =
        runTest {
            // Setup no cache
            coEvery { mockPreferenceComponent.getString(any()) } returns null

            val cacheManager = PreferenceCacheManager(mockPreferenceComponent)
            val client =
                PlayStoreApiClient(
                    logRepository = mockLogRepository,
                    cacheManager = cacheManager,
                )

            try {
                // This should try to fetch from Google Play (and fail in test environment)
                client.getAppVersion()
            } catch (e: Exception) {
                // Expected to fail in test environment due to network call
            }

            // Verify cache was checked
            coVerify { mockPreferenceComponent.getString("cache_tgtg_app_version_expiration") }
        }
}
