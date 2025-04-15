package com.cereal.command.monitor.data.zalando.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZalandoProduct(
    @SerialName("brand")
    val brand: Brand = Brand(),
    @SerialName("color")
    val color: String = "",
    @SerialName("@context")
    val context: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("image")
    val image: List<String> = listOf(),
    @SerialName("itemCondition")
    val itemCondition: String = "",
    @SerialName("manufacturer")
    val manufacturer: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("offers")
    val offers: List<Offer> = listOf(),
    @SerialName("sku")
    val sku: String = "",
    @SerialName("@type")
    val type: String = "",
    @SerialName("url")
    val url: String = "",
)
