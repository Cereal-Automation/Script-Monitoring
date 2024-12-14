package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeoMetadata(
    @SerialName("body")
    val body: String = "",
    @SerialName("title")
    val title: String = "",
)
