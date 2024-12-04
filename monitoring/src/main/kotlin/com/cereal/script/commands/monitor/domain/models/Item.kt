package com.cereal.script.commands.monitor.domain.models

data class Item(
    val id: String,
    val url: String,
    val name: String,
    val values: List<ItemValue> = listOf(),
)
