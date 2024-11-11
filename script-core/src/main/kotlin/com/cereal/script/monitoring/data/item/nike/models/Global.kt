package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Global(
    @SerialName("metaTags")
    val metaTags: MetaTags = MetaTags(),
)
