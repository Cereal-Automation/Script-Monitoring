package com.cereal.command.monitor.data.shopify.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("body_html")
    val bodyHtml: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("handle")
    val handle: String? = null,
    @SerialName("id")
    val id: String,
    @SerialName("images")
    val images: List<Image>,
    @SerialName("options")
    val options: List<Option?>? = null,
    @SerialName("product_type")
    val productType: String? = null,
    @SerialName("published_at")
    val publishedAt: Instant? = null,
    @SerialName("tags")
    val tags: List<String?>? = null,
    @SerialName("title")
    val title: String,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("variants")
    val variants: List<Variant> = emptyList(),
    @SerialName("vendor")
    val vendor: String? = null,
)
