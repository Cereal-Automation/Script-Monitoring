package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Link(
    @SerialName("href")
    val href: String = "",
    @SerialName("rel")
    val rel: String = "",
)
