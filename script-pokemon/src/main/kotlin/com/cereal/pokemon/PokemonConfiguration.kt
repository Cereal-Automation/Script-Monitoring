package com.cereal.pokemon

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem

interface PokemonConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = BaseConfiguration.KEY_MONITOR_INTERVAL,
        name = "Interval (seconds)",
        description = "How often the script checks bol.com, in seconds. Minimum value is 15 seconds.",
    )
    override fun monitorInterval(): Int? = 300

    @ScriptConfigurationItem(
        keyName = KEY_MAX_PRICE,
        name = "Max Price (EUR)",
        description = "Only notify for products at or below this price in EUR. Leave empty to monitor all releases and restocks regardless of price.",
    )
    fun maxPrice(): Int?

    companion object {
        const val KEY_MAX_PRICE = "max_price"
    }
}
