package com.cereal.rss

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.Visibility
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PriceThresholdStateModifierTest {
    private class InMemoryScriptConfig(private val configuration: Map<String, ScriptConfigValue>) : ScriptConfig {
        override fun valueForKey(key: String): ScriptConfigValue = configuration[key] ?: ScriptConfigValue.NullScriptConfigValue
    }

    @Test
    fun `getError returns null when getVisibility is not VisibleRequired`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(false),
                        RssConfiguration.KEY_PRICE_THRESHOLD to ScriptConfigValue.StringScriptConfigValue("-5.0"),
                    ),
                )
            val error = PriceThresholdStateModifier.getError(config)
            assertNull(error)
        }

    @Test
    fun `getError returns null when value is not a StringScriptConfigValue`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(true),
                        RssConfiguration.KEY_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(true),
                    ),
                )
            val error = PriceThresholdStateModifier.getError(config)
            assertNull(error)
        }

    @Test
    fun `getError returns error message when price threshold is an invalid number string`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(true),
                        RssConfiguration.KEY_PRICE_THRESHOLD to ScriptConfigValue.StringScriptConfigValue("abc"),
                    ),
                )
            val error = PriceThresholdStateModifier.getError(config)
            assertEquals("Price threshold must be a valid number.", error)
        }

    @Test
    fun `getError returns error message when price threshold is zero`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(true),
                        RssConfiguration.KEY_PRICE_THRESHOLD to ScriptConfigValue.StringScriptConfigValue("0.0"),
                    ),
                )
            val error = PriceThresholdStateModifier.getError(config)
            assertEquals("Price threshold must be greater than zero.", error)
        }

    @Test
    fun `getError returns error message when price threshold is negative`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(true),
                        RssConfiguration.KEY_PRICE_THRESHOLD to ScriptConfigValue.StringScriptConfigValue("-10.5"),
                    ),
                )
            val error = PriceThresholdStateModifier.getError(config)
            assertEquals("Price threshold must be greater than zero.", error)
        }

    @Test
    fun `getError returns null when price threshold is a valid positive number`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(true),
                        RssConfiguration.KEY_PRICE_THRESHOLD to ScriptConfigValue.StringScriptConfigValue("12.99"),
                    ),
                )
            val error = PriceThresholdStateModifier.getError(config)
            assertNull(error)
        }

    @Test
    fun `getVisibility returns VisibleRequired when monitor price threshold is true`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(true),
                    ),
                )
            val visibility = PriceThresholdStateModifier.getVisibility(config)
            assertEquals(Visibility.VisibleRequired, visibility)
        }

    @Test
    fun `getVisibility returns Hidden when monitor price threshold is false`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.BooleanScriptConfigValue(false),
                    ),
                )
            val visibility = PriceThresholdStateModifier.getVisibility(config)
            assertEquals(Visibility.Hidden, visibility)
        }

    @Test
    fun `getVisibility returns Hidden when monitor price threshold is not boolean`() =
        runTest {
            val config =
                InMemoryScriptConfig(
                    mapOf(
                        RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD to ScriptConfigValue.StringScriptConfigValue("true"),
                    ),
                )
            val visibility = PriceThresholdStateModifier.getVisibility(config)
            assertEquals(Visibility.Hidden, visibility)
        }
}
