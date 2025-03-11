package com.cereal.command.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublishedContent(
    @SerialName("collectionGroupId")
    val collectionGroupId: String = "",
    @SerialName("createdDateTime")
    val createdDateTime: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("language")
    val language: String = "",
    @SerialName("marketplace")
    val marketplace: String = "",
    @SerialName("nodes")
    val nodes: List<com.cereal.command.monitor.data.snkrs.models.Node> = listOf(),
    @SerialName("payloadType")
    val payloadType: String = "",
    @SerialName("preview")
    val preview: Boolean = false,
    @SerialName("publishEndDate")
    val publishEndDate: String = "",
    @SerialName("publishStartDate")
    val publishStartDate: String = "",
    @SerialName("resourceType")
    val resourceType: String = "",
    @SerialName("subType")
    val subType: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("version")
    val version: String = "",
    @SerialName("viewStartDate")
    val viewStartDate: String = "",
)
