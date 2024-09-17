package com.cereal.sample

import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem

interface SampleConfiguration : ScriptConfiguration {

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_INTERVAL,
        name = "Interval",
        description = "The duration, in seconds, the script waits before rechecking for updates. Minimum value is 1 second, defaults to 5 seconds."
    )
    fun monitorInterval(): Int?

    companion object {
        const val KEY_MONITOR_INTERVAL = "monitor_interval"
    }
}
