package com.cereal.script.utils.configuration

import com.cereal.script.utils.configuration.fixtures.InMemoryScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.Visibility
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MonitorIntervalStateModifierTest {
    private val intervalKey = BaseConfiguration.KEY_MONITOR_INTERVAL

    @Test
    fun `getError returns null when interval is greater than zero`() =
        runTest {
            val config = InMemoryScriptConfig(mapOf(intervalKey to ScriptConfigValue.IntScriptConfigValue(15)))
            val error = MonitorIntervalStateModifier.getError(config)
            assertNull(error)
        }

    @Test
    fun `getError returns error message when interval is less than 15`() =
        runTest {
            val config = InMemoryScriptConfig(mapOf(intervalKey to ScriptConfigValue.IntScriptConfigValue(0)))
            val error = MonitorIntervalStateModifier.getError(config)
            assertEquals("Interval must be at least 15 second.", error)
        }

    @Test
    fun `getError returns null when ScriptConfig does not contain interval`() =
        runTest {
            val config = InMemoryScriptConfig(mapOf())
            val error = MonitorIntervalStateModifier.getError(config)
            assertNull(error)
        }

    @Test
    fun `getError returns null when interval is a different type`() =
        runTest {
            val config =
                InMemoryScriptConfig(mapOf(intervalKey to ScriptConfigValue.StringScriptConfigValue("invalid")))
            val error = MonitorIntervalStateModifier.getError(config)
            assertNull(error)
        }

    @Test
    fun `getVisibility returns VisibleOptional`() =
        runTest {
            val config = InMemoryScriptConfig(mapOf())
            val visibility = MonitorIntervalStateModifier.getVisibility(config)
            assertEquals(Visibility.VisibleOptional, visibility)
        }
}
