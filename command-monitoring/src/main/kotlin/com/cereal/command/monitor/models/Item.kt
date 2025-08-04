package com.cereal.command.monitor.models

data class Item(
    val id: String,
    val url: String?,
    val name: String,
    val description: String? = null,
    var imageUrl: String? = null,
    val variants: List<Variant> = emptyList(),
    val properties: List<ItemProperty> = emptyList(),
)
