package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesXXX(
    @SerialName("actions")
    val actions: ActionsX? = ActionsX(),
    @SerialName("body")
    val body: Body? = Body(),
    @SerialName("experienceText")
    val experienceText: ExperienceText? = ExperienceText(),
    @SerialName("subtitle")
    val subtitle: Subtitle? = Subtitle(),
    @SerialName("title")
    val title: Title? = Title(),
)
