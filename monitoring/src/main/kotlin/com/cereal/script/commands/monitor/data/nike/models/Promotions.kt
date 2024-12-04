package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Promotions(
    @SerialName("promotionId")
    val promotionId: String = "",
    @SerialName("visibilities")
    val visibilities: List<Visibility> = listOf(),
)
