package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    @SerialName("rendered")
    val rendered: Boolean = false,
    @SerialName("requestFailed")
    val requestFailed: Boolean = false,
)
