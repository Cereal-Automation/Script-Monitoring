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
        const val KEY_RANDOM_PROXY = "random_proxy"
    }
}
