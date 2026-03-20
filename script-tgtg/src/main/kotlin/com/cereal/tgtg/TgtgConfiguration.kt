package com.cereal.tgtg

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.proxy.RandomProxy

interface TgtgConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_EMAIL,
        name = "Email",
        description = "Your TGTG account email address for authentication.",
        isScriptIdentifier = true,
        stateModifier = EmailStateModifier::class,
    )
    fun email(): String

    @ScriptConfigurationItem(
        keyName = KEY_LATITUDE,
        name = "Latitude",
        description =
            "Latitude coordinate for your location (e.g., 52.3676 for Amsterdam). To find it use a lookup " +
                "site such as https://latlong.net.",
        stateModifier = LatitudeStateModifier::class,
    )
    fun latitude(): Double

    @ScriptConfigurationItem(
        keyName = KEY_LONGITUDE,
        name = "Longitude",
        description =
            "Longitude coordinate for your location (e.g., 4.9041 for Amsterdam). To find it use a lookup " +
                "site such as https://latlong.net.",
        stateModifier = LongitudeStateModifier::class,
    )
    fun longitude(): Double

    @ScriptConfigurationItem(
        keyName = KEY_RADIUS,
        name = "Search Radius (meters)",
        description = "Search radius in meters around your location.",
        stateModifier = RadiusStateModifier::class,
    )
    fun radius(): Int? = 50000

    @ScriptConfigurationItem(
        keyName = KEY_FAVORITES_ONLY,
        name = "Favorites Only",
        description =
            "If enabled, only checks for available bags from your favorite stores. If disabled, checks all " +
                "stores within the search radius.",
    )
    fun favoritesOnly(): Boolean = true

    @ScriptConfigurationItem(
        keyName = KEY_RANDOM_PROXY,
        name = "Proxies",
        description =
            "The proxy to use when accessing the TGTG API. If multiple proxies are available, " +
                "they will be rotated after each run.",
    )
    fun proxy(): RandomProxy?

    @ScriptConfigurationItem(
        keyName = KEY_MINIMUM_RATING,
        name = "Minimum Rating",
        description = "Minimum average rating (0.0 to 5.0). Stores with a lower rating will be ignored.",
        stateModifier = MinimumRatingStateModifier::class,
    )
    fun minimumRating(): Double?

    @ScriptConfigurationItem(
        keyName = KEY_NOTIFY_ON_PRICE_CHANGE,
        name = "Notify on Price Change",
        description =
            "If enabled, sends a notification whenever the price of an available bag changes (Dynamic Price). " +
                "Only fires when the item is in stock.",
    )
    fun notifyOnPriceChange(): Boolean = false

    companion object {
        const val KEY_EMAIL = "email"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_RADIUS = "radius"
        const val KEY_FAVORITES_ONLY = "favorites_only"
        const val KEY_RANDOM_PROXY = "random_proxy"
        const val KEY_MINIMUM_RATING = "minimum_rating"
        const val KEY_NOTIFY_ON_PRICE_CHANGE = "notify_on_price_change"
    }
}
