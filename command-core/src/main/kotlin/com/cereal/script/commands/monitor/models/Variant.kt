package com.cereal.script.commands.monitor.models

data class Variant(
    val name: String,
    val inStock: Boolean,
    val stockLevel: String? = null,
)
