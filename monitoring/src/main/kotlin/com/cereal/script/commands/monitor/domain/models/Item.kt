package com.cereal.script.commands.monitor.domain.models

data class Item(
    val id: String,
    val url: String,
    val name: String,
    val description: String?,
    var imageUrl: String?,
    val properties: List<ItemProperty> = listOf(),
)
