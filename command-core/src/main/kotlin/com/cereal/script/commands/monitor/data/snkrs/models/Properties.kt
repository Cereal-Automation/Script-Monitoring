package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    @SerialName("altText")
    val altText: String = "",
    @SerialName("colorTheme")
    val colorTheme: String = "",
    @SerialName("copyId")
    val copyId: String = "",
    @SerialName("imageCaption")
    val imageCaption: String? = "",
    @SerialName("landscapeId")
    val landscapeId: String? = "",
    @SerialName("landscapeURL")
    val landscapeURL: String = "",
    @SerialName("portraitId")
    val portraitId: String? = "",
    @SerialName("portraitURL")
    val portraitURL: String = "",
    @SerialName("squarishId")
    val squarishId: String? = "",
    @SerialName("squarishURL")
    val squarishURL: String = "",
    @SerialName("subtitle")
    val subtitle: String = "",
    @SerialName("title")
    val title: String = "",
)
