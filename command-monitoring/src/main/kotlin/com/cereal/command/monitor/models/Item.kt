package com.cereal.command.monitor.models

data class Item(
    val id: String,
    val url: String?,
    val name: String,
    val description: String? = null,
    var imageUrl: String? = null,
    val properties: List<com.cereal.command.monitor.models.ItemProperty> = listOf(),
)
