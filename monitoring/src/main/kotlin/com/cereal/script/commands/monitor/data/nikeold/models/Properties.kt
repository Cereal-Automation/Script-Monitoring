package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Properties(
    @SerialName("actions")
    val actions: List<Action> = listOf(),
    @SerialName("body")
    val body: String = "",
    @SerialName("cardKey")
    val cardKey: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("portraitURL")
    val portraitURL: String = "",
    @SerialName("squarishURL")
    val squarishURL: String = "",
    @SerialName("style")
    val style: Style =
        Style(),
    @SerialName("threadId")
    val threadId: String = "",
    @SerialName("threadKey")
    val threadKey: String = "",
    @SerialName("title")
    val title: String = "",
)
