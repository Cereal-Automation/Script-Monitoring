package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    @SerialName("alternateName")
    val alternateName: String = "",
    @SerialName("attributeId")
    val attributeId: String = "",
    @SerialName("displayText")
    val displayText: String = "",
    @SerialName("expanded")
    val expanded: Boolean = false,
    @SerialName("options")
    val options: List<Option> = listOf(),
    @SerialName("optionsCount")
    val optionsCount: Int = 0,
    @SerialName("selected")
    val selected: Boolean = false,
    @SerialName("showMoreAfter")
    val showMoreAfter: Int = 0,
)
