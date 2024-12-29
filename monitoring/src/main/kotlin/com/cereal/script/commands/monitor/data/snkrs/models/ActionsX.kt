package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionsX(
    @SerialName("items")
    val items: List<Item>? = null,
)
