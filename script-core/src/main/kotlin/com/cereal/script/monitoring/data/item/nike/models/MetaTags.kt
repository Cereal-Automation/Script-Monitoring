package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaTags(
    @SerialName("defaultTitle")
    val defaultTitle: String = "",
    @SerialName("link")
    val link: List<Link> = listOf(),
    @SerialName("meta")
    val meta: List<Meta> = listOf(),
    @SerialName("title")
    val title: String = "",
    @SerialName("titleTemplate")
    val titleTemplate: String = "",
    @SerialName("urlPath")
    val urlPath: String = "",
)
