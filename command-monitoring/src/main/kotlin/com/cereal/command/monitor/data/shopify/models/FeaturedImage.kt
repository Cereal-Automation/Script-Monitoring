package com.cereal.command.monitor.data.shopify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeaturedImage(
    @SerialName("alt")
    val alt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("id")
    val id: Long? = null,
    @SerialName("position")
    val position: Int? = null,
    @SerialName("product_id")
    val productId: Long? = null,
    @SerialName("src")
    val src: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("variant_ids")
    val variantIds: List<Long?>? = null,
    @SerialName("width")
    val width: Int? = null,
)
