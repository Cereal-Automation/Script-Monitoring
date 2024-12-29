package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Node(
    @SerialName("analytics")
    val analytics: Analytics = Analytics(),
    @SerialName("id")
    val id: String = "",
    @SerialName("nodes")
    val nodes: List<NodeX>? = listOf(),
    @SerialName("properties")
    val properties: PropertiesXX = PropertiesXX(),
    @SerialName("subType")
    val subType: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("version")
    val version: String = "",
)
