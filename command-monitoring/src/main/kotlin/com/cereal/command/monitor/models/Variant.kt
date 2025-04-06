package com.cereal.command.monitor.models

data class Variant(
    val id: String,
    val name: String,
    val properties: List<ItemProperty>,
)
