package com.cereal.nike.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Copy(
    @SerialName("title")
    val title: String = "",
)
