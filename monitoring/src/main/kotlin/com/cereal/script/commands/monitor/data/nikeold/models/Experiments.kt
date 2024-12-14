package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Experiments(
    @SerialName("activeExperiments")
    val activeExperiments: ActiveExperiments =
        ActiveExperiments(),
    @SerialName("forcedExperiments")
    val forcedExperiments: ForcedExperiments =
        ForcedExperiments(),
)
