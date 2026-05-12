package com.cereal.command.monitor.models

import java.math.BigDecimal

sealed class ItemFilter {
    data class PriceAtMost(val value: BigDecimal) : ItemFilter()
    data class PriceAtLeast(val value: BigDecimal) : ItemFilter()
    data class CustomValueAtLeast(val name: String, val value: Double) : ItemFilter()
    data class CustomValueAtMost(val name: String, val value: Double) : ItemFilter()
    data class CustomValueEquals(val name: String, val value: String) : ItemFilter()
}

private fun parseNumericPrefix(raw: String): Double? =
    Regex("""^[\d.]+""").find(raw.trim())?.value?.toDoubleOrNull()

fun Item.passes(filters: List<ItemFilter>): Boolean =
    filters.all { filter ->
        when (filter) {
            is ItemFilter.PriceAtMost -> {
                val price = getValue<ItemProperty.Price>() ?: return@all false
                price.value <= filter.value
            }
            is ItemFilter.PriceAtLeast -> {
                val price = getValue<ItemProperty.Price>() ?: return@all false
                price.value >= filter.value
            }
            is ItemFilter.CustomValueAtLeast -> {
                val custom = properties
                    .filterIsInstance<ItemProperty.Custom>()
                    .firstOrNull { it.name == filter.name } ?: return@all false
                val numeric = parseNumericPrefix(custom.value) ?: return@all false
                numeric >= filter.value
            }
            is ItemFilter.CustomValueAtMost -> {
                val custom = properties
                    .filterIsInstance<ItemProperty.Custom>()
                    .firstOrNull { it.name == filter.name } ?: return@all false
                val numeric = parseNumericPrefix(custom.value) ?: return@all false
                numeric <= filter.value
            }
            is ItemFilter.CustomValueEquals -> {
                val custom = properties
                    .filterIsInstance<ItemProperty.Custom>()
                    .firstOrNull { it.name == filter.name } ?: return@all false
                custom.value.equals(filter.value, ignoreCase = true)
            }
        }
    }
