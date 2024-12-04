package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CmsContent(
    @SerialName("headline")
    val headline: Headline =
        Headline(),
    @SerialName("seo_copy_block")
    val seoCopyBlock: SeoCopyBlock =
        SeoCopyBlock(),
    @SerialName("seo_metadata")
    val seoMetadata: SeoMetadata =
        SeoMetadata(),
)
