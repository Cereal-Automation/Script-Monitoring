package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("copy")
    val copy: Copy =
        Copy(),
    @SerialName("globalProductId")
    val globalProductId: String = "",
    @SerialName("pdpUrl")
    val pdpUrl: PdpUrl =
        PdpUrl(),
    @SerialName("prices")
    val prices: Prices =
        Prices(),
    @SerialName("colorwayImages")
    val colorwayImages: ColorwayImages =
        ColorwayImages(),
    @SerialName("displayColors")
    val displayColors: DisplayColors =
        DisplayColors(),
    @SerialName("productCode")
    val productCode: String = "",
)
