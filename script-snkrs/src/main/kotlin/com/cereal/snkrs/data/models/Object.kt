package com.cereal.snkrs.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Object(
    @SerialName("channelId")
    val channelId: String = "",
    @SerialName("channelName")
    val channelName: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("language")
    val language: String = "",
    @SerialName("lastFetchTime")
    val lastFetchTime: String = "",
    @SerialName("marketplace")
    val marketplace: String = "",
    @SerialName("productInfo")
    val productInfo: List<ProductInfo>? = listOf(),
    @SerialName("publishedContent")
    val publishedContent: com.cereal.snkrs.data.models.PublishedContent =
        com.cereal.snkrs.data.models
            .PublishedContent(),
    @SerialName("resourceType")
    val resourceType: String = "",
)
