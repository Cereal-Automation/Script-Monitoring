package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Seo(
    @SerialName("description")
    val description: String = "",
    @SerialName("doNotIndex")
    val doNotIndex: Boolean = false,
    @SerialName("keywords")
    val keywords: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("title")
    val title: String = "",
)
