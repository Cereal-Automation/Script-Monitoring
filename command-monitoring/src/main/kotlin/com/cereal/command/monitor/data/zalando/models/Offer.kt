package com.cereal.command.monitor.data.zalando.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Offer(
    @SerialName("availability")
    val availability: String = "",
    @SerialName("price")
    val price: String = "",
    @SerialName("priceCurrency")
    val priceCurrency: String = "",
    @SerialName("sku")
    val sku: String = "",
    @SerialName("@type")
    val type: String = "",
    @SerialName("url")
    val url: String = "",
)
