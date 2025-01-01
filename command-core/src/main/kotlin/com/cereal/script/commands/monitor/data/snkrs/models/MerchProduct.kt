package com.cereal.script.commands.monitor.data.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MerchProduct(
    @SerialName("brand")
    val brand: String = "",
    @SerialName("catalogId")
    val catalogId: String = "",
    @SerialName("channels")
    val channels: List<String> = listOf(),
    @SerialName("colorCode")
    val colorCode: String = "",
    @SerialName("comingSoonCountdownClock")
    val comingSoonCountdownClock: Boolean = false,
    @SerialName("commerceCountryExclusions")
    val commerceCountryExclusions: List<String> = listOf(),
    @SerialName("commerceEndDate")
    val commerceEndDate: String? = "",
    @SerialName("commercePublishDate")
    val commercePublishDate: String? = "",
    @SerialName("commerceStartDate")
    val commerceStartDate: String = "",
    @SerialName("exclusiveAccess")
    val exclusiveAccess: Boolean = false,
    @SerialName("genders")
    val genders: List<String> = listOf(),
    @SerialName("hardLaunch")
    val hardLaunch: Boolean = false,
    @SerialName("hideFromCSR")
    val hideFromCSR: Boolean = false,
    @SerialName("hideFromSearch")
    val hideFromSearch: Boolean = false,
    @SerialName("hidePayment")
    val hidePayment: Boolean = false,
    @SerialName("id")
    val id: String = "",
    @SerialName("inventoryOverride")
    val inventoryOverride: Boolean = false,
    @SerialName("inventoryShareOff")
    val inventoryShareOff: Boolean = false,
    @SerialName("isAppleWatch")
    val isAppleWatch: Boolean = false,
    @SerialName("isAttributionApproved")
    val isAttributionApproved: Boolean = false,
    @SerialName("isCopyAvailable")
    val isCopyAvailable: Boolean = false,
    @SerialName("isCustomsApproved")
    val isCustomsApproved: Boolean = false,
    @SerialName("isImageAvailable")
    val isImageAvailable: Boolean = false,
    @SerialName("isPromoExclusionMessage")
    val isPromoExclusionMessage: Boolean = false,
    @SerialName("labelName")
    val labelName: String = "",
    @SerialName("mainColor")
    val mainColor: Boolean = false,
    @SerialName("merchGroup")
    val merchGroup: String = "",
    @SerialName("modificationDate")
    val modificationDate: String = "",
    @SerialName("notifyMeIndicator")
    val notifyMeIndicator: Boolean = false,
    @SerialName("pid")
    val pid: String = "",
    @SerialName("preOrder")
    val preOrder: Boolean = false,
    @SerialName("productGroupId")
    val productGroupId: String = "",
    @SerialName("productType")
    val productType: String = "",
    @SerialName("publishType")
    val publishType: String = "",
    @SerialName("quantityLimit")
    val quantityLimit: Int = 0,
    @SerialName("resourceType")
    val resourceType: String = "",
    @SerialName("sizeConverterId")
    val sizeConverterId: String = "",
    @SerialName("sizeGuideId")
    val sizeGuideId: String = "",
    @SerialName("snapshotId")
    val snapshotId: String = "",
    @SerialName("softLaunchDate")
    val softLaunchDate: String? = "",
    @SerialName("sportTags")
    val sportTags: List<String> = listOf(),
    @SerialName("status")
    val status: String = "",
    @SerialName("styleCode")
    val styleCode: String = "",
    @SerialName("styleColor")
    val styleColor: String = "",
    @SerialName("styleType")
    val styleType: String = "",
)
