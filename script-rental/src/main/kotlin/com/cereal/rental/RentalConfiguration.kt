package com.cereal.rental

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

private object RentalIntervalStateModifier : StateModifier {
    override fun getError(scriptConfig: ScriptConfig): String? {
        val value = scriptConfig.valueForKey(BaseConfiguration.KEY_MONITOR_INTERVAL)
        return if (value is ScriptConfigValue.IntScriptConfigValue && value.value < 60) {
            "Interval must be at least 60 seconds (1 minute)."
        } else {
            null
        }
    }

    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleOptional
}

interface RentalConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = BaseConfiguration.KEY_MONITOR_INTERVAL,
        name = "Interval",
        description = "The duration, in seconds, the script waits before rechecking for updates. Minimum value is 60 seconds.",
        stateModifier = RentalIntervalStateModifier::class,
    )
    override fun monitorInterval(): Int? = 300

    @ScriptConfigurationItem(
        keyName = KEY_CITIES,
        name = "Cities",
        description = "Comma-separated city names to monitor, e.g. amsterdam,rotterdam,utrecht",
        isScriptIdentifier = true,
    )
    fun cities(): String

    @ScriptConfigurationItem(
        keyName = KEY_MAX_PRICE,
        name = "Max Price (EUR/month)",
        description = "Maximum monthly rent in EUR. Leave empty for no limit.",
    )
    fun maxPrice(): Int?

    @ScriptConfigurationItem(
        keyName = KEY_MIN_SIZE_M2,
        name = "Min Size (m²)",
        description = "Minimum apartment size in square metres. Leave empty for no limit.",
    )
    fun minSizeM2(): Int?

    @ScriptConfigurationItem(
        keyName = KEY_MIN_ROOMS,
        name = "Min Rooms",
        description = "Minimum number of rooms. Leave empty for no limit.",
    )
    fun minRooms(): Int?

    @ScriptConfigurationItem(
        keyName = KEY_ENABLE_PARARIUS,
        name = "Enable Pararius",
        description = "If enabled, scrape Pararius.com for new listings.",
    )
    fun enablePararius(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_ENABLE_FUNDA,
        name = "Enable Funda",
        description = "If enabled, scrape Funda.nl for new listings.",
    )
    fun enableFunda(): Boolean

    companion object {
        const val KEY_CITIES = "cities"
        const val KEY_MAX_PRICE = "max_price"
        const val KEY_MIN_SIZE_M2 = "min_size_m2"
        const val KEY_MIN_ROOMS = "min_rooms"
        const val KEY_ENABLE_PARARIUS = "enable_pararius"
        const val KEY_ENABLE_FUNDA = "enable_funda"
    }
}
