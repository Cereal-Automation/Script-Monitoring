package com.cereal.tgtg

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

object EmailStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val value = scriptConfig.valueForKey(TgtgConfiguration.KEY_EMAIL)
        return if (value is ScriptConfigValue.StringScriptConfigValue) {
            if (value.value.isBlank()) {
                "Email cannot be empty."
            } else if (!value.value.contains("@")) {
                "Invalid email address."
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleRequired
}

object LatitudeStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val value = scriptConfig.valueForKey(TgtgConfiguration.KEY_LATITUDE)
        return if (value is ScriptConfigValue.DoubleScriptConfigValue) {
            if (value.value < -90.0 || value.value > 90.0) {
                "Latitude must be between -90 and 90."
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleRequired
}

object LongitudeStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val value = scriptConfig.valueForKey(TgtgConfiguration.KEY_LONGITUDE)
        return if (value is ScriptConfigValue.DoubleScriptConfigValue) {
            if (value.value < -180.0 || value.value > 180.0) {
                "Longitude must be between -180 and 180."
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleRequired
}

object RadiusStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val value = scriptConfig.valueForKey(TgtgConfiguration.KEY_RADIUS)
        return if (value is ScriptConfigValue.IntScriptConfigValue) {
            if (value.value <= 0) {
                "Radius must be greater than 0."
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleOptional
}
