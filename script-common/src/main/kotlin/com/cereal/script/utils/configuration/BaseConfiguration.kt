package com.cereal.script.utils.configuration

import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem

interface BaseConfiguration : ScriptConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_INTERVAL,
        name = "Interval",
        description =
            "The duration, in seconds, the script waits before rechecking for updates. Minimum value is 15" +
                "second, defaults to a random value between 15 and 30 seconds.",
        stateModifier = MonitorIntervalStateModifier::class,
    )
    fun monitorInterval(): Int?

    companion object {
        const val KEY_MONITOR_INTERVAL = "monitor_interval"
    }
}
