package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SelectedConcept(
    @SerialName("alternateName")
    val alternateName: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
)
