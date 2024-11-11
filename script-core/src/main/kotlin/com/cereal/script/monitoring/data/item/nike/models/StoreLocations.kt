package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreLocations(
    @SerialName("filteredByStore")
    val filteredByStore: Boolean = false,
    @SerialName("postalCode")
    val postalCode: String = "",
    @SerialName("selectedStore")
    val selectedStore: SelectedStore = SelectedStore(),
)
