package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefaultStyleX(
    @SerialName("textLocation")
    val textLocation: TextLocation? = null,
)
