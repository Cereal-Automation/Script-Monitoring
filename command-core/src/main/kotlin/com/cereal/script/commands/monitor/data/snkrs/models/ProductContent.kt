package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductContent(
    @SerialName("athletes")
    val athletes: List<Athlete> = listOf(),
    @SerialName("colorDescription")
    val colorDescription: String = "",
    @SerialName("colors")
    val colors: List<Color> = listOf(),
    @SerialName("description")
    val description: String = "",
    @SerialName("descriptionHeading")
    val descriptionHeading: String = "",
    @SerialName("fullTitle")
    val fullTitle: String = "",
    @SerialName("globalPid")
    val globalPid: String = "",
    @SerialName("langLocale")
    val langLocale: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("subtitle")
    val subtitle: String = "",
    @SerialName("techSpec")
    val techSpec: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("widths")
    val widths: List<Width> = listOf(),
)
