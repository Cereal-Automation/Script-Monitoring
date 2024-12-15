package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LimitRetailExperience(
    @SerialName("disabledStoreOfferingCodes")
    val disabledStoreOfferingCodes: List<String> = listOf(),
    @SerialName("value")
    val value: String = "",
)
