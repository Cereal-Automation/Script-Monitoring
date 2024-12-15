package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublishedContent(
    @SerialName("analytics")
    val analytics: Analytics = Analytics(),
    @SerialName("collectionGroupId")
    val collectionGroupId: String = "",
    @SerialName("createdDateTime")
    val createdDateTime: String = "",
    @SerialName("externalReferences")
    val externalReferences: List<ExternalReference> = listOf(),
    @SerialName("id")
    val id: String = "",
    @SerialName("language")
    val language: String = "",
    @SerialName("links")
    val links: LinksXXXXX = LinksXXXXX(),
    @SerialName("marketplace")
    val marketplace: String = "",
    @SerialName("nodes")
    val nodes: List<Node> = listOf(),
    @SerialName("payloadType")
    val payloadType: String = "",
    @SerialName("preview")
    val preview: Boolean = false,
    @SerialName("properties")
    val properties: PropertiesXXXX = PropertiesXXXX(),
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
