package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("initialized")
    val initialized: Boolean = false,
    @SerialName("isLoggedIn")
    val isLoggedIn: Boolean = false,
    @SerialName("isSwoosh")
    val isSwoosh: Boolean = false,
)
