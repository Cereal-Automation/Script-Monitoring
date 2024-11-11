package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rollout(
    @SerialName("experiments")
    val experiments: List<ExperimentX> = listOf(),
    @SerialName("id")
    val id: String = "",
)
