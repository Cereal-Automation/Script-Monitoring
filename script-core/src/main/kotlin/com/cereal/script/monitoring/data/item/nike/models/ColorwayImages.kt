package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorwayImages(
    @SerialName("portraitURL")
    val portraitURL: String = "",
    @SerialName("squarishURL")
    val squarishURL: String = "",
)
