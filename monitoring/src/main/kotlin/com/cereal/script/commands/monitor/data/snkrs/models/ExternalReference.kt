package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalReference(
    @SerialName("domain")
    val domain: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("resource")
    val resource: String = "",
)
