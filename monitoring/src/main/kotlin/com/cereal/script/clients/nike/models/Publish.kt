package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Publish(
    @SerialName("collectionGroups")
    val collectionGroups: List<String> = listOf(),
    @SerialName("collections")
    val collections: List<String> = listOf(),
    @SerialName("countries")
    val countries: List<String> = listOf(),
    @SerialName("pageId")
    val pageId: String = "",
)
