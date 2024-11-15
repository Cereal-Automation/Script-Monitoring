package com.cereal.nike

import com.cereal.script.monitoring.data.item.nike.ScrapeCategory
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.shared.BaseConfiguration

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

    companion object {
        const val KEY_CATEGORY = "category"
        const val KEY_MONITOR_PRICE = "monitor_price"
        const val KEY_MONITOR_NEW_PRODUCTS = "monitor_new_products"
    }
}
