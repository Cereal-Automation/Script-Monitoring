package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesXX(
    @SerialName("actions")
    val actions: List<Action> = listOf(),
    @SerialName("altText")
    val altText: String? = "",
    @SerialName("aspectRatio")
    val aspectRatio: Double? = 0.0,
    @SerialName("assetId")
    val assetId: String? = "",
    @SerialName("autoPlay")
    val autoPlay: Boolean? = false,
    @SerialName("body")
    val body: String? = "",
    @SerialName("colorTheme")
    val colorTheme: String = "",
    @SerialName("containerType")
    val containerType: String? = "",
    @SerialName("copyId")
    val copyId: String = "",
    @SerialName("custom")
    val custom: Custom? = Custom(),
    @SerialName("imageCaption")
    val imageCaption: String? = "",
    @SerialName("inactive")
    val inactive: Boolean? = false,
    @SerialName("internalName")
    val internalName: String? = "",
    @SerialName("jsonBody")
    val jsonBody: JsonBody? = JsonBody(),
    @SerialName("landscape")
    val landscape: LandscapeX? = LandscapeX(),
    @SerialName("landscapeId")
    val landscapeId: String? = "",
    @SerialName("landscapeURL")
    val landscapeURL: String? = "",
    @SerialName("loop")
    val loop: Boolean? = false,
    @SerialName("manifestURL")
    val manifestURL: String? = "",
    @SerialName("portrait")
    val portrait: PortraitX? = PortraitX(),
    @SerialName("portraitId")
    val portraitId: String? = "",
    @SerialName("portraitURL")
    val portraitURL: String? = "",
    @SerialName("providerId")
    val providerId: String? = "",
    @SerialName("secondaryPortrait")
    val secondaryPortrait: SecondaryPortraitX? = SecondaryPortraitX(),
    @SerialName("speed")
    val speed: Int? = 0,
    @SerialName("squarish")
    val squarish: SquarishX? = SquarishX(),
    @SerialName("squarishId")
    val squarishId: String? = "",
    @SerialName("squarishURL")
    val squarishURL: String? = "",
    @SerialName("startImage")
    val startImage: StartImage? = StartImage(),
    @SerialName("style")
    val style: StyleX? = StyleX(),
    @SerialName("subtitle")
    val subtitle: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("videoId")
    val videoId: String? = "",
)
