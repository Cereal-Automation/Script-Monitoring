package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Navigation(
    @SerialName("canonicalUrl")
    val canonicalUrl: String = "",
    @SerialName("pageUrl")
    val pageUrl: String = "",
    @SerialName("path")
    val path: String = "",
)
