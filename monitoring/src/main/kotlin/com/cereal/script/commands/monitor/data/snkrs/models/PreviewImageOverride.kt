package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreviewImageOverride(
    @SerialName("aspectRatio")
    val aspectRatio: Double = 0.0,
    @SerialName("assetId")
    val assetId: String = "",
    @SerialName("height")
    val height: Int = 0,
    @SerialName("type")
    val type: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("width")
    val width: Int = 0,
)
