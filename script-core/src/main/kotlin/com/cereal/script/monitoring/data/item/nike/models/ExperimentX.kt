package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExperimentX(
    @SerialName("audienceConditions")
    val audienceConditions: List<String> = listOf(),
    @SerialName("audienceIds")
    val audienceIds: List<String> = listOf(),
    @SerialName("forcedVariations")
    val forcedVariations: ForcedVariationsX? = ForcedVariationsX(),
    @SerialName("id")
    val id: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("layerId")
    val layerId: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("trafficAllocation")
    val trafficAllocation: List<TrafficAllocation> = listOf(),
    @SerialName("variations")
    val variations: List<VariationX> = listOf(),
)
