package com.cereal.script.utils.configuration

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class BaseConfigurationTest {
    private class TestBaseConfiguration(
        private val interval: Int?,
    ) : BaseConfiguration {
        override fun monitorInterval(): Int? = interval
    }

    @Test
    fun `test monitorInterval returns configured value`() =
        runTest {
            // Given a configuration with a specific interval
            val expectedInterval = 15
            val configuration = TestBaseConfiguration(expectedInterval)

            // When getting the monitor interval
            val actualInterval = configuration.monitorInterval()

            // Then it should match the configured value
            assertEquals(expectedInterval, actualInterval)
        }

    @Test
    fun `test monitorInterval returns null when not configured`() =
        runTest {
            // Given a configuration with no interval set
            val configuration = TestBaseConfiguration(null)

            // When getting the monitor interval
            val actualInterval = configuration.monitorInterval()

            // Then it should be null
            assertNull(actualInterval)
        }

    @Test
    fun `test KEY_MONITOR_INTERVAL constant value`() =
        runTest {
            // The constant should match the expected value
            assertEquals("monitor_interval", BaseConfiguration.KEY_MONITOR_INTERVAL)
        }
}
