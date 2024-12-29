package com.cereal.snkrs

import com.cereal.script.commands.monitor.data.snkrs.Locale
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.proxy.RandomProxy
import com.cereal.shared.BaseConfiguration

interface SnkrsConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_LOCATION,
        name = "Location",
        description = "The SNKRS location to monitor.",
    )
    fun locale(): Locale

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_PRICE,
        name = "Monitor price drops",
        description = "If enabled, send a notification when the price of a product changes.",
    )
    fun monitorPriceDrops(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_IN_STOCK,
        name = "Monitor stock changes",
        description =
            "If enabled, a notification will be sent whenever a product (or one of its sizes) gets in stock.",
    )
    fun monitorStockChanges(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_RANDOM_PROXY,
        name = "Proxies",
        description =
            "The proxy to use when reading the SNKRS api. If multiple proxies are available, they will" +
                "be rotated after each run.",
    )
    fun proxy(): RandomProxy

    companion object {
        const val KEY_LOCATION = "location"
        const val KEY_MONITOR_PRICE = "monitor_price"
        const val KEY_MONITOR_IN_STOCK = "monitor_in_stock"
        const val KEY_RANDOM_PROXY = "random_proxy"
    }
}
