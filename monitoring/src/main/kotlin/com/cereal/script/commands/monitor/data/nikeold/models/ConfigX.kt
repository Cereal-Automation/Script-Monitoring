package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigX(
    @SerialName("asyncChatEnabled")
    val asyncChatEnabled: Boolean = false,
    @SerialName("isGlobalStoreCountry")
    val isGlobalStoreCountry: Boolean = false,
    @SerialName("useMarketplaceId")
    val useMarketplaceId: Boolean = false,
)
