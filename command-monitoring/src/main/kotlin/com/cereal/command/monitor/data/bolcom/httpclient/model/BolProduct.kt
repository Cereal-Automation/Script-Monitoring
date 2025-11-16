package com.cereal.command.monitor.data.bolcom.httpclient.model

import kotlinx.serialization.Serializable

@Serializable
data class BolProduct(
    val productId: String,
    val slug: String?,
    val title: String?,
    val brand: String?,
    val price: Double?,
    val discount: Double?,
    val regularPrice: Double?,
    val orderable: Boolean,
    val imageUrl: String?,
    val seller: String?,
    val description: String?,
)
