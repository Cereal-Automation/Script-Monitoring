package com.cereal.nike.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wall(
    @SerialName("pageData")
    val pageData: PageData =
        PageData(),
    @SerialName("productGroupings")
    val productGroupings: List<ProductGrouping> = listOf(),
    @SerialName("pages")
    val pages: PageData =
        PageData(),
)
