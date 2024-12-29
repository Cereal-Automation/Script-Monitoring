package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesXXXXXX(
    @SerialName("actions")
    val actions: Actions? = Actions(),
    @SerialName("body")
    val body: Body? = Body(),
    @SerialName("experienceText")
    val experienceText: ExperienceText? = ExperienceText(),
    @SerialName("subtitle")
    val subtitle: SubtitleX? = SubtitleX(),
    @SerialName("title")
    val title: TitleX? = TitleX(),
)
