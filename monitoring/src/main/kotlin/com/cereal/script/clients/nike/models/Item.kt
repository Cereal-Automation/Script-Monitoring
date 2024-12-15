package com.cereal.script.clients.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("buttonStyle")
    val buttonStyle: String = "",
    @SerialName("id")
    val id: String = "",
)
