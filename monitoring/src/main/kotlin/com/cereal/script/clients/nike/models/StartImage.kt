package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartImage(
    @SerialName("altText")
    val altText: String = "",
    @SerialName("landscape")
    val landscape: LandscapeXX = LandscapeXX(),
    @SerialName("portrait")
    val portrait: PortraitXX = PortraitXX(),
    @SerialName("secondaryPortrait")
    val secondaryPortrait: SecondaryPortraitX = SecondaryPortraitX(),
    @SerialName("squarish")
    val squarish: SquarishXX = SquarishXX()
)