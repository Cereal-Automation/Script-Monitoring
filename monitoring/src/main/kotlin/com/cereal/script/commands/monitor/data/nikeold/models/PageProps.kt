package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageProps(
    @SerialName("cloudUrlFragment")
    val cloudUrlFragment: String = "",
    @SerialName("initialState")
    val initialState: InitialState =
        InitialState(),
)
