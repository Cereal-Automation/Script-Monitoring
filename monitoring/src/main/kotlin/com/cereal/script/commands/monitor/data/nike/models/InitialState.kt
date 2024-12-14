package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitialState(
    @SerialName("Auth")
    val auth: Auth =
        Auth(),
    @SerialName("banner")
    val banner: Banner =
        Banner(),
    @SerialName("Common")
    val common: Common =
        Common(),
    @SerialName("Config")
    val config: Config =
        Config(),
    @SerialName("Device")
    val device: Device =
        Device(),
    @SerialName("Experiments")
    val experiments: Experiments =
        Experiments(),
    @SerialName("Favorites")
    val favorites: Favorites =
        Favorites(),
    @SerialName("global")
    val global: Global =
        Global(),
    @SerialName("intl")
    val intl: Intl =
        Intl(),
    @SerialName("localization")
    val localization: Localization =
        Localization(),
    @SerialName("Privacy")
    val privacy: Privacy =
        Privacy(),
    @SerialName("StoreLocations")
    val storeLocations: StoreLocations =
        StoreLocations(),
    @SerialName("User")
    val user: User =
        User(),
    @SerialName("Wall")
    val wall: Wall =
        Wall(),
)
