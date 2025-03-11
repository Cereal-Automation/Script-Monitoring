package com.cereal.command.monitor.models

data class Variant(
    val name: String,
    val inStock: Boolean,
    val stockLevel: String? = null,
)
