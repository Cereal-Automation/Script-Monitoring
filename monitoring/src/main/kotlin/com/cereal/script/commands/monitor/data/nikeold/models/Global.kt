package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Global(
    @SerialName("metaTags")
    val metaTags: MetaTags =
        MetaTags(),
)
