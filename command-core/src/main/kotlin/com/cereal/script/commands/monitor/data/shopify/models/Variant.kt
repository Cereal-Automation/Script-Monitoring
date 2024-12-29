package com.cereal.script.commands.monitor.data.shopify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Variant(
    @SerialName("available")
    val available: Boolean,
    @SerialName("compare_at_price")
    val compareAtPrice: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("featured_image")
    val featuredImage: FeaturedImage? = null,
    @SerialName("grams")
    val grams: Int? = null,
    @SerialName("id")
    val id: Long? = null,
    @SerialName("option1")
    val option1: String? = null,
    @SerialName("option2")
    val option2: String? = null,
    @SerialName("position")
    val position: Int? = null,
    @SerialName("price")
    val price: String? = null,
    @SerialName("product_id")
    val productId: Long? = null,
    @SerialName("requires_shipping")
    val requiresShipping: Boolean? = null,
    @SerialName("sku")
    val sku: String? = null,
    @SerialName("taxable")
    val taxable: Boolean? = null,
    @SerialName("title")
    val title: String,
    @SerialName("updated_at")
    val updatedAt: String? = null,
)
