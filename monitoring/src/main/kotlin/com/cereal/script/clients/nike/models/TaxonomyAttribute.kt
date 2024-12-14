package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaxonomyAttribute(
    @SerialName("ids")
    val ids: List<String> = listOf(),
    @SerialName("resourceType")
    val resourceType: String = ""
)