package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorAvatar(
    @SerialName("aspectRatio")
    val aspectRatio: Int? = null,
    @SerialName("assetId")
    val assetId: String = "",
    @SerialName("focalPoint")
    val focalPoint: FocalPoint? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String = "",
    @SerialName("width")
    val width: Int? = null
)