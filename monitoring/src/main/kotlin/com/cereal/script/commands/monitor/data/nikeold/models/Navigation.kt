package com.cereal.script.commands.monitor.data.nikeold.models

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
