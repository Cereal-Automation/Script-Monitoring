package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Copy(
    @SerialName("subTitle")
    val subTitle: String = "",
    @SerialName("title")
    val title: String = "",
)
