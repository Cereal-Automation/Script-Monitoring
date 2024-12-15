package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payload(
    @SerialName("authorAvatar")
    val authorAvatar: AuthorAvatar = AuthorAvatar(),
    @SerialName("authorByline")
    val authorByline: String = "",
    @SerialName("authorName")
    val authorName: String = "",
    @SerialName("frameDuration")
    val frameDuration: Int = 0,
    @SerialName("previewImageOverride")
    val previewImageOverride: PreviewImageOverride = PreviewImageOverride(),
    @SerialName("previewSubtitleOverride")
    val previewSubtitleOverride: String = "",
    @SerialName("previewTitleOverride")
    val previewTitleOverride: String = "",
)
