package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Auth(
    @SerialName("bearer")
    val bearer: String = "",
)