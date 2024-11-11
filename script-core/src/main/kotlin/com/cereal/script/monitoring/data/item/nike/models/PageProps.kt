package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageProps(
    @SerialName("cloudUrlFragment")
    val cloudUrlFragment: String = "",
    @SerialName("initialState")
    val initialState: InitialState = InitialState(),
)
