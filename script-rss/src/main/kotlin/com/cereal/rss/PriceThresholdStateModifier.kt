package com.cereal.rss

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

object PriceThresholdStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        if (getVisibility(scriptConfig) != Visibility.VisibleRequired) {
            return null
        }
        val priceThreshold = scriptConfig.valueForKey(RssConfiguration.KEY_PRICE_THRESHOLD)
        if (priceThreshold !is ScriptConfigValue.StringScriptConfigValue) {
            return null
        }
        val decimalValue = priceThreshold.value.toBigDecimalOrNull() ?: return "Price threshold must be a valid number."
        if (decimalValue <= java.math.BigDecimal.ZERO) {
            return "Price threshold must be greater than zero."
        }
        return null
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility {
        val monitorPriceThreshold = scriptConfig.valueForKey(RssConfiguration.KEY_MONITOR_PRICE_THRESHOLD)
        return if (monitorPriceThreshold is ScriptConfigValue.BooleanScriptConfigValue && monitorPriceThreshold.value) {
            Visibility.VisibleRequired
        } else {
            Visibility.Hidden
        }
    }
}
