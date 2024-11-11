package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NikeResponse(
    @SerialName("assetPrefix")
    val assetPrefix: String = "",
    @SerialName("buildId")
    val buildId: String = "",
    @SerialName("customServer")
    val customServer: Boolean = false,
    @SerialName("gssp")
    val gssp: Boolean = false,
    @SerialName("isFallback")
    val isFallback: Boolean = false,
    @SerialName("page")
    val page: String = "",
    @SerialName("props")
    val props: Props = Props(),
    @SerialName("query")
    val query: Query = Query(),
)
