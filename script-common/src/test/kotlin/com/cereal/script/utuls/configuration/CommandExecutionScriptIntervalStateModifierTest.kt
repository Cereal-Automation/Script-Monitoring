package com.cereal.script.utuls.configuration

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.script.utils.configuration.MonitorIntervalStateModifier
import com.cereal.script.utuls.configuration.fixtures.InMemoryScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CommandExecutionScriptIntervalStateModifierTest {
    /**
     * `MonitorIntervalStateModifier` is a `StateModifier` that applies some rules to the `ScriptConfig`.
     * One of these rules is that the interval defined by `SampleConfiguration.KEY_MONITOR_INTERVAL` must be at least 1.
     *
     * `MonitorIntervalStateModifier.getError(scriptConfig: ScriptConfig): String?` is a function that validates the script configuration.
     * If the configuration does not meet the interval condition, it returns an error message. If the configuration is valid, it returns `null`.
     */

    private val intervalKey = BaseConfiguration.KEY_MONITOR_INTERVAL

    @Test
    fun `getError returns null when interval is greater than zero`() =
        runTest {
            val config = InMemoryScriptConfig(mapOf(intervalKey to ScriptConfigValue.IntScriptConfigValue(2)))
            val error = MonitorIntervalStateModifier.getError(config)
            assertNull(error)
        }

    @Test
    fun `getError returns error message when interval is less than one`() =
        runTest {
            val config = InMemoryScriptConfig(mapOf(intervalKey to ScriptConfigValue.IntScriptConfigValue(0)))
            val error = MonitorIntervalStateModifier.getError(config)
            assertEquals("Interval must be at least 1 second.", error)
        }

    @Test
    fun `getError returns null when ScriptConfig does not contain interval`() =
        runTest {
            val config = InMemoryScriptConfig(mapOf())
            val error = MonitorIntervalStateModifier.getError(config)
            assertNull(error)
        }
}
