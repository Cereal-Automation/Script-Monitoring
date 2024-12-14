package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Privacy(
    @SerialName("arePerformanceCookiesAllowed")
    val arePerformanceCookiesAllowed: Boolean = false,
    @SerialName("areSocialCookiesAllowed")
    val areSocialCookiesAllowed: Boolean = false,
    @SerialName("hasEUCookie")
    val hasEUCookie: Boolean = false,
)
