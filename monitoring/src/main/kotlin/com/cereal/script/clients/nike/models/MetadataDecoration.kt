package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetadataDecoration(
    @SerialName("id")
    val id: String = "",
    @SerialName("namespace")
    val namespace: String = "",
    @SerialName("payload")
    val payload: Payload = Payload()
)