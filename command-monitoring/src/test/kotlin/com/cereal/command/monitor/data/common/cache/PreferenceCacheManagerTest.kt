package com.cereal.command.monitor.data.common.cache

import com.cereal.sdk.component.preference.PreferenceComponent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PreferenceCacheManagerTest {
    private lateinit var mockPreferenceComponent: PreferenceComponent
    private lateinit var cacheManager: PreferenceCacheManager

    @BeforeEach
    fun setUp() {
        mockPreferenceComponent = mockk<PreferenceComponent>(relaxed = true)
        cacheManager = PreferenceCacheManager(mockPreferenceComponent)
    }

    @Test
    fun `store should save value and expiration time with correct keys`() =
        runTest {
            // Given
            val key = "test_key"
            val value = "test_value"
            val expiration = 1.hours

            // When
            cacheManager.store(key, value, expiration)

            // Then
            coVerify {
                mockPreferenceComponent.setString("cache_${key}_value", value)
            }
            coVerify {
                mockPreferenceComponent.setString("cache_${key}_expiration", any())
            }
        }

    @Test
    fun `retrieve should return value when cache is valid`() =
        runTest {
            // Given
            val key = "test_key"
            val value = "test_value"
            val futureTime = (System.currentTimeMillis() / 1000) + 3600 // 1 hour in future

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns futureTime.toString()
            coEvery { mockPreferenceComponent.getString("cache_${key}_value") } returns value

            // When
            val result = cacheManager.retrieve(key)

            // Then
            assertEquals(value, result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
            coVerify { mockPreferenceComponent.getString("cache_${key}_value") }
        }

    @Test
    fun `retrieve should return null when cache is expired`() =
        runTest {
            // Given
            val key = "test_key"
            val pastTime = (System.currentTimeMillis() / 1000) - 3600 // 1 hour in past

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns pastTime.toString()

            // When
            val result = cacheManager.retrieve(key)

            // Then
            assertNull(result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
            coVerify(exactly = 0) { mockPreferenceComponent.getString("cache_${key}_value") }
        }

    @Test
    fun `retrieve should return null when cache does not exist`() =
        runTest {
            // Given
            val key = "non_existent_key"

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns null

            // When
            val result = cacheManager.retrieve(key)

            // Then
            assertNull(result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
            coVerify(exactly = 0) { mockPreferenceComponent.getString("cache_${key}_value") }
        }

    @Test
    fun `isValid should return true when cache exists and is not expired`() =
        runTest {
            // Given
            val key = "test_key"
            val futureTime = (System.currentTimeMillis() / 1000) + 3600 // 1 hour in future

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns futureTime.toString()

            // When
            val result = cacheManager.isValid(key)

            // Then
            assertTrue(result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
        }

    @Test
    fun `isValid should return false when cache is expired`() =
        runTest {
            // Given
            val key = "test_key"
            val pastTime = (System.currentTimeMillis() / 1000) - 3600 // 1 hour in past

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns pastTime.toString()

            // When
            val result = cacheManager.isValid(key)

            // Then
            assertFalse(result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
        }

    @Test
    fun `isValid should return false when cache does not exist`() =
        runTest {
            // Given
            val key = "non_existent_key"

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns null

            // When
            val result = cacheManager.isValid(key)

            // Then
            assertFalse(result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
        }

    @Test
    fun `isValid should return false when expiration string is malformed`() =
        runTest {
            // Given
            val key = "test_key"
            val malformedExpiration = "not_a_number"

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns malformedExpiration

            // When
            val result = cacheManager.isValid(key)

            // Then
            assertFalse(result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
        }

    @Test
    fun `isValid should return false when expiration string is empty`() =
        runTest {
            // Given
            val key = "test_key"
            val emptyExpiration = ""

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns emptyExpiration

            // When
            val result = cacheManager.isValid(key)

            // Then
            assertFalse(result)
            coVerify { mockPreferenceComponent.getString("cache_${key}_expiration") }
        }

    @Test
    fun `store should handle different expiration durations correctly`() =
        runTest {
            // Given
            val key = "test_key"
            val value = "test_value"
            val shortExpiration = 5.minutes
            val longExpiration = 2.hours

            // When
            cacheManager.store(key, value, shortExpiration)
            cacheManager.store("${key}_long", value, longExpiration)

            // Then
            coVerify {
                mockPreferenceComponent.setString("cache_${key}_value", value)
                mockPreferenceComponent.setString("cache_${key}_expiration", any())
            }
            coVerify {
                mockPreferenceComponent.setString("cache_${key}_long_value", value)
                mockPreferenceComponent.setString("cache_${key}_long_expiration", any())
            }
        }

    @Test
    fun `store should handle special characters in key and value`() =
        runTest {
            // Given
            val key = "test_key_with_special_chars_!@#$%^&*()"
            val value = "test_value_with_special_chars_!@#$%^&*()_and_unicode_🚀"
            val expiration = 1.hours

            // When
            cacheManager.store(key, value, expiration)

            // Then
            coVerify {
                mockPreferenceComponent.setString("cache_${key}_value", value)
            }
            coVerify {
                mockPreferenceComponent.setString("cache_${key}_expiration", any())
            }
        }

    @Test
    fun `retrieve should work with very short expiration times`() =
        runTest {
            // Given
            val key = "test_key"
            val value = "test_value"
            val shortExpiration = 1.seconds
            val futureTime = (System.currentTimeMillis() / 1000) + 2 // 2 seconds in future

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns futureTime.toString()
            coEvery { mockPreferenceComponent.getString("cache_${key}_value") } returns value

            // When
            val result = cacheManager.retrieve(key)

            // Then
            assertEquals(value, result)
        }

    @Test
    fun `isValid should handle edge case where expiration is exactly now`() =
        runTest {
            // Given
            val key = "test_key"
            val currentTime = System.currentTimeMillis() / 1000

            coEvery { mockPreferenceComponent.getString("cache_${key}_expiration") } returns currentTime.toString()

            // When
            val result = cacheManager.isValid(key)

            // Then
            // Should return false since expiration time is not in the future
            assertFalse(result)
        }
}
