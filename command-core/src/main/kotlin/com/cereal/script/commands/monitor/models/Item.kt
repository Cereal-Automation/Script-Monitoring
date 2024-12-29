package com.cereal.script.commands.monitor.models

data class Item(
    val id: String,
    val url: String?,
    val name: String,
    val description: String? = null,
    var imageUrl: String? = null,
    val properties: List<ItemProperty> = listOf(),
)
