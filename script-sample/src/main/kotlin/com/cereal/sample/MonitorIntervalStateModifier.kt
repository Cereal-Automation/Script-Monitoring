package com.cereal.sample

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

object MonitorIntervalStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val value = scriptConfig.valueForKey(SampleConfiguration.KEY_MONITOR_INTERVAL)

        if (value is ScriptConfigValue.IntScriptConfigValue && value.value < 1) {
            return "Interval must be at least 1 second."
        } else {
            return null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleOptional
}
