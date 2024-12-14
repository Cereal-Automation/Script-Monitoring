package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Link(
    @SerialName("href")
    val href: String = "",
    @SerialName("rel")
    val rel: String = "",
)
