package com.cereal.snkrs

import com.cereal.script.clients.snkrs.Locale
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
        keyName = KEY_MONITOR_NEW_PRODUCTS,
        name = "Monitor new products",
        description = "If enabled, send a notification when there is a new product.",
    )
    fun monitorNewProduct(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_STOCK_CHANGES,
        name = "Monitor stock changes",
        description = "If enabled, send a notification when the stock of a product (size) changes.",
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
        const val KEY_MONITOR_NEW_PRODUCTS = "monitor_new_products"
        const val KEY_MONITOR_STOCK_CHANGES = "monitor_stock_changes"
        const val KEY_RANDOM_PROXY = "random_proxy"
    }
}
