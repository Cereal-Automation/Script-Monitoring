package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryNames(
    @SerialName("en")
    val en: String = "",
)
