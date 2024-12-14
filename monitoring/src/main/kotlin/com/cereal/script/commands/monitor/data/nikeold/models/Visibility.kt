package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Visibility(
    @SerialName("contentThreadId")
    val contentThreadId: String = "",
    @SerialName("subtitle")
    val subtitle: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("visibilityEndDate")
    val visibilityEndDate: String = "",
    @SerialName("visibilityStartDate")
    val visibilityStartDate: String = "",
    @SerialName("visibilityType")
    val visibilityType: String = "",
)
