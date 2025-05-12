package com.cereal.command.monitor.models

data class MarketItem(
    val id: String,
    val url: String?,
    val variants: List<MarketItemVariant>,
)
