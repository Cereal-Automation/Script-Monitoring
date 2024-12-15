package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Search(
    @SerialName("conceptIds")
    val conceptIds: List<String> = listOf(),
)
