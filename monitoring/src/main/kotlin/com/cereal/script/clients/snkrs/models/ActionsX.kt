package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionsX(
    @SerialName("items")
    val items: List<Item>? = null,
)
