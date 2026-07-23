package com.cereal.nike.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageProps(
    @SerialName("initialState")
    val initialState: InitialState =
        InitialState(),
)
