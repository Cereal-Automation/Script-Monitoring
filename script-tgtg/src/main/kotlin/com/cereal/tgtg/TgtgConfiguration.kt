package com.cereal.tgtg

import com.cereal.script.utils.configuration.BaseConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.proxy.RandomProxy

interface TgtgConfiguration : BaseConfiguration {
    @ScriptConfigurationItem(
        keyName = KEY_EMAIL,
        name = "Email",
        description = "Your TGTG account email address for authentication.",
    )
    fun email(): String

    @ScriptConfigurationItem(
        keyName = KEY_LATITUDE,
        name = "Latitude",
        description = "Latitude coordinate for your location (e.g., 52.3676 for Amsterdam).",
    )
    fun latitude(): Double

    @ScriptConfigurationItem(
        keyName = KEY_LONGITUDE,
        name = "Longitude",
        description = "Longitude coordinate for your location (e.g., 4.9041 for Amsterdam).",
    )
    fun longitude(): Double

    @ScriptConfigurationItem(
        keyName = KEY_RADIUS,
        name = "Search Radius (meters)",
        description = "Search radius in meters around your location. Default is 50000 (50km).",
    )
    fun radius(): Int?

    @ScriptConfigurationItem(
        keyName = KEY_FAVORITES_ONLY,
        name = "Favorites Only",
        description = "If enabled, only fetch businesses you have marked as favorites.",
    )
    fun favoritesOnly(): Boolean

    @ScriptConfigurationItem(
        keyName = KEY_RANDOM_PROXY,
        name = "Proxies",
        description =
            "The proxy to use when accessing the TGTG API. If multiple proxies are available, " +
                "they will be rotated after each run.",
    )
    fun proxy(): RandomProxy?

    companion object {
        const val KEY_EMAIL = "email"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_RADIUS = "radius"
        const val KEY_FAVORITES_ONLY = "favorites_only"
        const val KEY_RANDOM_PROXY = "random_proxy"
    }
}
