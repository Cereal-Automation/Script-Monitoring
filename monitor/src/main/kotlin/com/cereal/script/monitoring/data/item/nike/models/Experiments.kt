package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Experiments(
    @SerialName("activeExperiments")
    val activeExperiments: ActiveExperiments = ActiveExperiments(),
    @SerialName("forcedExperiments")
    val forcedExperiments: ForcedExperiments = ForcedExperiments(),
)
