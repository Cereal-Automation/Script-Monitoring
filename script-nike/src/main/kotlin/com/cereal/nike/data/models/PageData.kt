package com.cereal.nike.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageData(
    @SerialName("next")
    val next: String = "",
)
