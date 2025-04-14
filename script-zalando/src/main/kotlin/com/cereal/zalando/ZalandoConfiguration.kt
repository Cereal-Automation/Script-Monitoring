package com.cereal.zalando

import com.cereal.command.monitor.data.zalando.ZalandoMonitorType
import com.cereal.command.monitor.data.zalando.ZalandoProductCategory
import com.cereal.command.monitor.data.zalando.ZalandoWebsite
import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.proxy.RandomProxy

interface ZalandoConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_WEBSITE,
        name = "Website",
        description = "The website to monitor.",
    )
    fun website(): ZalandoWebsite

    @ScriptConfigurationItem(
        keyName = KEY_CATEGORY,
        name = "Category",
        description = "The category to monitor.",
    )
    fun category(): ZalandoProductCategory

    @ScriptConfigurationItem(
        keyName = KEY_MONITOR_TYPE,
        name = "What to monitor",
        description = "What do you want to monitor?",
    )
    fun monitorType(): ZalandoMonitorType

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
        const val KEY_WEBSITE = "website"
        const val KEY_CATEGORY = "category"
        const val KEY_MONITOR_TYPE = "monitor_type"
        const val KEY_MONITOR_PRICE = "monitor_price"
        const val KEY_MONITOR_NEW_PRODUCTS = "monitor_new_products"
        const val KEY_RANDOM_PROXY = "random_proxy"
    }
}
