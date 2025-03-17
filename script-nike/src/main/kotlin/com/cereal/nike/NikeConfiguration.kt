package com.cereal.nike

import com.cereal.command.monitor.data.nike.ScrapeCategory
import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.proxy.RandomProxy

interface NikeConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_CATEGORY,
        name = "Category",
        description = "The category to monitor.",
    )
    fun category(): ScrapeCategory

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
        keyName = KEY_RANDOM_PROXY,
        name = "Proxies",
        description =
            "The proxy to use when scraping the Nike website. If multiple proxies are available, they will" +
                "be rotated after each run.",
    )
    fun proxy(): RandomProxy

    companion object {
        const val KEY_CATEGORY = "category"
        const val KEY_MONITOR_PRICE = "monitor_price"
        const val KEY_MONITOR_NEW_PRODUCTS = "monitor_new_products"
        const val KEY_RANDOM_PROXY = "random_proxy"
    }
}
