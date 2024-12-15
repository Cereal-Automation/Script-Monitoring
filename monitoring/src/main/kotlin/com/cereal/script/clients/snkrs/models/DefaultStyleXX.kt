package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefaultStyleXX(
    @SerialName("textLocation")
    val textLocation: TextLocation? = TextLocation(),
)
