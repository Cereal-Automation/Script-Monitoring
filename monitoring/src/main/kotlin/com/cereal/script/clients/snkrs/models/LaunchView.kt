package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LaunchView(
    @SerialName("delayConsumerVisibilityUntil")
    val delayConsumerVisibilityUntil: String? = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("links")
    val links: Links = Links(),
    @SerialName("method")
    val method: String = "",
    @SerialName("paymentMethod")
    val paymentMethod: String = "",
    @SerialName("productId")
    val productId: String = "",
    @SerialName("resourceType")
    val resourceType: String = "",
    @SerialName("startEntryDate")
    val startEntryDate: String = "",
    @SerialName("stopEntryDate")
    val stopEntryDate: String? = "",
)
