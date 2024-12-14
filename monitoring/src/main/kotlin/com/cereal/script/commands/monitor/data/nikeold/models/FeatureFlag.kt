package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlag(
    @SerialName("experimentIds")
    val experimentIds: List<String> = listOf(),
    @SerialName("id")
    val id: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("rolloutId")
    val rolloutId: String = "",
    @SerialName("variables")
    val variables: List<Variable> = listOf(),
)
