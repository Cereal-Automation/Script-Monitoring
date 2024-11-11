package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("assetPaths")
    val assetPaths: AssetPaths = AssetPaths(),
    @SerialName("cacheDuration")
    val cacheDuration: CacheDuration = CacheDuration(),
    @SerialName("channelId")
    val channelId: String = "",
    @SerialName("dark")
    val dark: Boolean = false,
    @SerialName("division")
    val division: String = "",
    @SerialName("domain")
    val domain: String = "",
    @SerialName("env")
    val env: String = "",
    @SerialName("facebookAppId")
    val facebookAppId: String = "",
    @SerialName("flags")
    val flags: Flags = Flags(),
    @SerialName("globalConfig")
    val globalConfig: GlobalConfig = GlobalConfig(),
    @SerialName("gqlHash")
    val gqlHash: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("platform")
    val platform: String = "",
    @SerialName("preview")
    val preview: Boolean = false,
    @SerialName("version")
    val version: String = "",
)
