package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClearAllFilters(
    @SerialName("crawlable")
    val crawlable: Boolean = false,
    @SerialName("navigation")
    val navigation: Navigation =
        Navigation(),
    @SerialName("navigationAttributeIds")
    val navigationAttributeIds: List<String> = listOf(),
)
