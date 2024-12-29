package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NodeX(
    @SerialName("analytics")
    val analytics: Analytics = Analytics(),
    @SerialName("id")
    val id: String = "",
    @SerialName("properties")
    val properties: Properties = Properties(),
    @SerialName("subType")
    val subType: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("version")
    val version: String = "",
)