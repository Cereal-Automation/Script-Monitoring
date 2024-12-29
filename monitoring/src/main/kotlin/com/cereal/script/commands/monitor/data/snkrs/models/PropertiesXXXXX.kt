package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesXXXXX(
    @SerialName("altText")
    val altText: String = "",
    @SerialName("colorTheme")
    val colorTheme: String = "",
    @SerialName("copyId")
    val copyId: String = "",
    @SerialName("custom")
    val custom: Custom? = Custom(),
    @SerialName("imageCaption")
    val imageCaption: String? = "",
    @SerialName("internalName")
    val internalName: String? = "",
    @SerialName("landscape")
    val landscape: LandscapeX = LandscapeX(),
    @SerialName("landscapeId")
    val landscapeId: String = "",
    @SerialName("landscapeURL")
    val landscapeURL: String = "",
    @SerialName("portrait")
    val portrait: Portrait = Portrait(),
    @SerialName("portraitId")
    val portraitId: String = "",
    @SerialName("portraitURL")
    val portraitURL: String = "",
    @SerialName("secondaryPortrait")
    val secondaryPortrait: SecondaryPortraitXXX? = SecondaryPortraitXXX(),
    @SerialName("squarish")
    val squarish: SquarishX = SquarishX(),
    @SerialName("squarishId")
    val squarishId: String = "",
    @SerialName("squarishURL")
    val squarishURL: String = "",
    @SerialName("style")
    val style: StyleXX = StyleXX(),
    @SerialName("subtitle")
    val subtitle: String = "",
    @SerialName("title")
    val title: String = "",
)
