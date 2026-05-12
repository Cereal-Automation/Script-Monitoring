package com.cereal.rental

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

object RentalSourceStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val pararius = scriptConfig.valueForKey(RentalConfiguration.KEY_ENABLE_PARARIUS)
        val funda = scriptConfig.valueForKey(RentalConfiguration.KEY_ENABLE_FUNDA)

        val parariusEnabled = (pararius as? ScriptConfigValue.BooleanScriptConfigValue)?.value == true
        val fundaEnabled = (funda as? ScriptConfigValue.BooleanScriptConfigValue)?.value == true

        return if (!parariusEnabled && !fundaEnabled) {
            "At least one source (Pararius or Funda) must be enabled."
        } else {
            null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleRequired
}
