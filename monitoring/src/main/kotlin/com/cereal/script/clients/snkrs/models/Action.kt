package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Action(
    @SerialName("actionText")
    val actionText: String? = "",
    @SerialName("actionType")
    val actionType: String = "",
    @SerialName("analytics")
    val analytics: Analytics = Analytics(),
    @SerialName("destination")
    val destination: Destination = Destination(),
    @SerialName("destinationId")
    val destinationId: String = "",
    @SerialName("destinationType")
    val destinationType: String? = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("product")
    val product: ProductX? = ProductX(),
)
