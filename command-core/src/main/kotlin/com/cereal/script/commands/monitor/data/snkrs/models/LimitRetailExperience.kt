package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LimitRetailExperience(
    @SerialName("disabledStoreOfferingCodes")
    val disabledStoreOfferingCodes: List<String> = listOf(),
    @SerialName("value")
    val value: String = "",
)
