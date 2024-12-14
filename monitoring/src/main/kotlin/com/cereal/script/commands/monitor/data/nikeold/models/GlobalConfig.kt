package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalConfig(
    @SerialName("cacheTag")
    val cacheTag: String = "",
    @SerialName("config")
    val config: ConfigX =
        ConfigX(),
)
