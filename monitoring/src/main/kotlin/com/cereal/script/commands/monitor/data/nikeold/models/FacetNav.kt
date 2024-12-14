package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FacetNav(
    @SerialName("categories")
    val categories: List<Category> = listOf(),
    @SerialName("clearAllFilters")
    val clearAllFilters: ClearAllFilters =
        ClearAllFilters(),
    @SerialName("filters")
    val filters: List<Filter> = listOf(),
    @SerialName("pageName")
    val pageName: String = "",
    @SerialName("selectedConcepts")
    val selectedConcepts: List<SelectedConcept> = listOf(),
)
