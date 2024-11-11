package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Common(
    @SerialName("channel")
    val channel: String = "",
    @SerialName("globalNavFixed")
    val globalNavFixed: Boolean = false,
    @SerialName("globalNavHidden")
    val globalNavHidden: Boolean = false,
    @SerialName("globalNavPeekabooEnabled")
    val globalNavPeekabooEnabled: Boolean = false,
    @SerialName("globalNavPeekabooVisible")
    val globalNavPeekabooVisible: Boolean = false,
    @SerialName("pageType")
    val pageType: String = "",
)
