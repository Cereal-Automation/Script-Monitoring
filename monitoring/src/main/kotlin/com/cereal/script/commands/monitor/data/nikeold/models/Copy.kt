package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Copy(
    @SerialName("subTitle")
    val subTitle: String = "",
    @SerialName("title")
    val title: String = "",
)
