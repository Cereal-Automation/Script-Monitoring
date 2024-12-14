package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreLocations(
    @SerialName("filteredByStore")
    val filteredByStore: Boolean = false,
    @SerialName("postalCode")
    val postalCode: String = "",
    @SerialName("selectedStore")
    val selectedStore: SelectedStore =
        SelectedStore(),
)
