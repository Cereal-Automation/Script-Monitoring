package com.cereal.command.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sku(
    @SerialName("catalogSkuId")
    val catalogSkuId: String = "",
    @SerialName("gtin")
    val gtin: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("merchGroup")
    val merchGroup: String = "",
    @SerialName("modificationDate")
    val modificationDate: String = "",
    @SerialName("nikeSize")
    val nikeSize: String = "",
    @SerialName("parentId")
    val parentId: String = "",
    @SerialName("parentType")
    val parentType: String = "",
    @SerialName("productId")
    val productId: String = "",
    @SerialName("resourceType")
    val resourceType: String = "",
    @SerialName("sizeConversionId")
    val sizeConversionId: String? = "",
    @SerialName("snapshotId")
    val snapshotId: String = "",
    @SerialName("stockKeepingUnitId")
    val stockKeepingUnitId: String = "",
    @SerialName("vatCode")
    val vatCode: String = "",
)
