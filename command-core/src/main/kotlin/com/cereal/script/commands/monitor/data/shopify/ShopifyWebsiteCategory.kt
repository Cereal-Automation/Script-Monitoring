package com.cereal.script.commands.monitor.data.shopify

data class ShopifyWebsiteCategory(
    val name: String,
    val url: String,
)

enum class HeadphoneZoneWebsite(
    val category: ShopifyWebsiteCategory,
) {
    DEALS_OF_THE_MONTH(
        ShopifyWebsiteCategory(
            "Deals of the Month",
            "https://www.headphonezone.in/pages/deals-of-the-month",
        ),
    ),
}
