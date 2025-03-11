package com.cereal.command.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageProps(
    @SerialName("initialState")
    val initialState: InitialState =
        InitialState(),
)
