package com.cereal.nike.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NikeResponse(
    @SerialName("page")
    val page: String = "",
    @SerialName("props")
    val props: Props =
        Props(),
)
