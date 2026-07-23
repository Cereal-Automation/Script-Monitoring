package com.cereal.nike.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorwayImages(
    @SerialName("portraitURL")
    val portraitURL: String? = null,
    @SerialName("squarishURL")
    val squarishURL: String? = null,
)
