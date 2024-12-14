package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MerchPrice(
    @SerialName("country")
    val country: String = "",
    @SerialName("currency")
    val currency: String = "",
    @SerialName("currentPrice")
    val currentPrice: String = "",
    @SerialName("discounted")
    val discounted: Boolean = false,
    @SerialName("fullPrice")
    val fullPrice: Double = 0.0,
    @SerialName("id")
    val id: String = "",
    @SerialName("links")
    val links: Links = Links(),
    @SerialName("modificationDate")
    val modificationDate: String = "",
    @SerialName("msrp")
    val msrp: Double = 0.0,
    @SerialName("parentId")
    val parentId: String = "",
    @SerialName("parentType")
    val parentType: String = "",
    @SerialName("productId")
    val productId: String = "",
    @SerialName("promoExclusions")
    val promoExclusions: List<String> = listOf(),
    @SerialName("promoInclusions")
    val promoInclusions: List<String> = listOf(),
    @SerialName("resourceType")
    val resourceType: String = "",
    @SerialName("snapshotId")
    val snapshotId: String = "",
)
