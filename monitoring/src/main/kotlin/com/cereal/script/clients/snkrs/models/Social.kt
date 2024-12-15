package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Social(
    @SerialName("comments")
    val comments: Boolean = false,
    @SerialName("likes")
    val likes: Boolean = false,
    @SerialName("share")
    val share: Boolean = false,
)
