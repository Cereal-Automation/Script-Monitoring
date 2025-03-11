package com.cereal.command.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitialState(
    @SerialName("Wall")
    val wall: Wall =
        Wall(),
)
