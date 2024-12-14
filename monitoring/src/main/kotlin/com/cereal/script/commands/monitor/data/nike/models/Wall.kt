package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wall(
    @SerialName("apiMetaData")
    val apiMetaData: ApiMetaData =
        ApiMetaData(),
    @SerialName("canonicalUrl")
    val canonicalUrl: String = "",
    @SerialName("cmsContent")
    val cmsContent: CmsContent =
        CmsContent(),
    @SerialName("datafile")
    val datafile: Datafile =
        Datafile(),
    @SerialName("facetNav")
    val facetNav: FacetNav =
        FacetNav(),
    @SerialName("isOptimizelyCompleted")
    val isOptimizelyCompleted: Boolean = false,
    @SerialName("loading")
    val loading: Boolean = false,
    @SerialName("pageData")
    val pageData: PageData =
        PageData(),
    @SerialName("primaryHeading")
    val primaryHeading: String = "",
    @SerialName("productGroupings")
    val productGroupings: List<ProductGrouping> = listOf(),
    @SerialName("selectedFiltersCount")
    val selectedFiltersCount: Int = 0,
    @SerialName("sortBy")
    val sortBy: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("traceId")
    val traceId: String = "",
    @SerialName("uuids")
    val uuids: List<String> = listOf(),
    @SerialName("pages")
    val pages: PageData =
        PageData(),
)
