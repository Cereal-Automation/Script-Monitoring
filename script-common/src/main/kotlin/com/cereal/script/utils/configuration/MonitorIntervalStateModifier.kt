package com.cereal.script.utils.configuration

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

object MonitorIntervalStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val value = scriptConfig.valueForKey(BaseConfiguration.KEY_MONITOR_INTERVAL)

        return if (value is ScriptConfigValue.IntScriptConfigValue && value.value < 15) {
            "Interval must be at least 15 second."
        } else {
            null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleOptional
}
