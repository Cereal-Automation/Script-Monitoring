package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Action(
    @SerialName("actionKey")
    val actionKey: String = "",
    @SerialName("actionText")
    val actionText: String? = null,
    @SerialName("actionType")
    val actionType: String = "",
    @SerialName("destination")
    val destination: Destination = Destination(),
)
