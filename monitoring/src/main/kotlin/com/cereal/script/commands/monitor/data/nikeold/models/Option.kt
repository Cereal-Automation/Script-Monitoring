package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Option(
    @SerialName("alternateName")
    val alternateName: String = "",
    @SerialName("attributeId")
    val attributeId: String = "",
    @SerialName("crawlable")
    val crawlable: Boolean = false,
    @SerialName("displayText")
    val displayText: String = "",
    @SerialName("navigation")
    val navigation: Navigation =
        Navigation(),
    @SerialName("navigationAttributeIds")
    val navigationAttributeIds: List<String> = listOf(),
    @SerialName("resultCount")
    val resultCount: Int = 0,
    @SerialName("selected")
    val selected: Boolean = false,
)
