package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Query(
    @SerialName("slug")
    val slug: List<String> = listOf(),
)
