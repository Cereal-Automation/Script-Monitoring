package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Analytics(
    @SerialName("hashKey")
    val hashKey: String = ""
)