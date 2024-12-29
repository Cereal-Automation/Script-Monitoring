package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortraitX(
    @SerialName("aspectRatio")
    val aspectRatio: Double? = null,
    @SerialName("assetId")
    val assetId: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("manifestURL")
    val manifestURL: String? = null,
    @SerialName("providerId")
    val providerId: String? = null,
    @SerialName("startImageUrl")
    val startImageUrl: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("videoId")
    val videoId: String? = null,
    @SerialName("view")
    val view: String? = null,
)
