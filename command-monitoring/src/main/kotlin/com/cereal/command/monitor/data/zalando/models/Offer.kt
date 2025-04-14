package com.cereal.command.monitor.data.zalando.models

import com.cereal.command.monitor.data.common.json.serializer.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
enum class Availability {
    @SerialName("http://schema.org/InStock")
    InStock,

    @SerialName("http://schema.org/OutOfStock")
    OutStock,
}

@Serializable
data class Offer(
    @SerialName("availability")
    val availability: Availability,
    @SerialName("price")
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    @SerialName("priceCurrency")
    val priceCurrency: String = "",
    @SerialName("sku")
    val sku: String = "",
    @SerialName("@type")
    val type: String = "",
    @SerialName("url")
    val url: String = "",
)
