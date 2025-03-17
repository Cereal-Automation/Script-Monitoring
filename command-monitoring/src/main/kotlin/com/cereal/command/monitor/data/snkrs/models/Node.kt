package com.cereal.command.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Node(
    @SerialName("id")
    val id: String = "",
    @SerialName("nodes")
    val nodes: List<Node>? = listOf(),
    @SerialName("subType")
    val subType: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("version")
    val version: String = "",
    @SerialName("properties")
    val properties: Properties = Properties(),
)
