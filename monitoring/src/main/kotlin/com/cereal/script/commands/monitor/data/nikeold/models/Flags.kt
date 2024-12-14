package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Flags(
    @SerialName("abTests")
    val abTests: Boolean = false,
    @SerialName("cookieSettings")
    val cookieSettings: Boolean = false,
    @SerialName("debug")
    val debug: Boolean = false,
    @SerialName("enableMAP")
    val enableMAP: Boolean = false,
    @SerialName("enableNikeShopModals")
    val enableNikeShopModals: Boolean = false,
    @SerialName("enablePercentOffDisplay")
    val enablePercentOffDisplay: Boolean = false,
    @SerialName("enablePromoMsg")
    val enablePromoMsg: Boolean = false,
    @SerialName("forcePriceDecimals")
    val forcePriceDecimals: Boolean = false,
    @SerialName("globalNavUseGeoPrivacy")
    val globalNavUseGeoPrivacy: Boolean = false,
    @SerialName("pulseInsights")
    val pulseInsights: Boolean = false,
    @SerialName("recommendations")
    val recommendations: Boolean = false,
    @SerialName("sendPIDsOnWall")
    val sendPIDsOnWall: Boolean = false,
    @SerialName("showFindInStore")
    val showFindInStore: Boolean = false,
    @SerialName("showGeoMismatch")
    val showGeoMismatch: Boolean = false,
    @SerialName("showJPStrikethrough")
    val showJPStrikethrough: Boolean = false,
    @SerialName("showPromoBanner")
    val showPromoBanner: Boolean = false,
    @SerialName("showRelatedCategories")
    val showRelatedCategories: Boolean = false,
    @SerialName("showRelatedContent")
    val showRelatedContent: Boolean = false,
    @SerialName("showSEOCopy")
    val showSEOCopy: Boolean = false,
    @SerialName("showUniteTimers")
    val showUniteTimers: Boolean = false,
    @SerialName("swooshEligibleGeo")
    val swooshEligibleGeo: Boolean = false,
    @SerialName("useDotcomNav")
    val useDotcomNav: Boolean = false,
    @SerialName("usePlainSaleMessaging")
    val usePlainSaleMessaging: Boolean = false,
    @SerialName("useServiceCanonicalUrl")
    val useServiceCanonicalUrl: Boolean = false,
    @SerialName("useUniteStaging")
    val useUniteStaging: Boolean = false,
    @SerialName("wallFavorites")
    val wallFavorites: Boolean = false,
    @SerialName("wallImpressions")
    val wallImpressions: Boolean = false,
    @SerialName("wallNoResultsCarousel")
    val wallNoResultsCarousel: Boolean = false,
    @SerialName("xSearchOptionsHeader")
    val xSearchOptionsHeader: Boolean = false,
)
