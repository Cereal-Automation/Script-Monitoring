package com.cereal.script.monitoring.data.item.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("badgeAttribute")
    val badgeAttribute: String? = null,
    @SerialName("badgeLabel")
    val badgeLabel: String? = null,
    @SerialName("colorwayImages")
    val colorwayImages: ColorwayImages = ColorwayImages(),
    @SerialName("consumerChannelId")
    val consumerChannelId: String = "",
    @SerialName("copy")
    val copy: Copy = Copy(),
    @SerialName("displayColors")
    val displayColors: DisplayColors = DisplayColors(),
    @SerialName("featuredAttributes")
    val featuredAttributes: List<String>? = null,
    @SerialName("globalProductId")
    val globalProductId: String = "",
    @SerialName("groupKey")
    val groupKey: String = "",
    @SerialName("internalPid")
    val internalPid: String = "",
    @SerialName("isNewUntil")
    val isNewUntil: String? = null,
    @SerialName("merchProductId")
    val merchProductId: String = "",
    @SerialName("pdpUrl")
    val pdpUrl: PdpUrl = PdpUrl(),
    @SerialName("prices")
    val prices: Prices = Prices(),
    @SerialName("productCode")
    val productCode: String = "",
    @SerialName("productSubType")
    val productSubType: String = "",
    @SerialName("productType")
    val productType: String = "",
    @SerialName("promotions")
    val promotions: Promotions? = null,
)
