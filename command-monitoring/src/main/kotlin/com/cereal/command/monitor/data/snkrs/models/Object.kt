package com.cereal.command.monitor.data.snkrs.models

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
    val publishedContent: com.cereal.command.monitor.data.snkrs.models.PublishedContent =
        com.cereal.command.monitor.data.snkrs.models
            .PublishedContent(),
    @SerialName("resourceType")
    val resourceType: String = "",
)
