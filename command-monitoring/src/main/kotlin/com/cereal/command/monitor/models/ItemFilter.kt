package com.cereal.command.monitor.models

import java.math.BigDecimal

/**
 * A filter that can be applied to an [Item] via [Item.passes].
 *
 * Filters are AND-composed: every filter in the list must pass for an item to be accepted.
 * If a required property is absent from the item, that filter evaluates to **false** (strict exclusion).
 */
sealed class ItemFilter {
    data class PriceAtMost(val value: BigDecimal) : ItemFilter()

    data class PriceAtLeast(val value: BigDecimal) : ItemFilter()

    data class CustomValueAtLeast(val name: String, val value: Double) : ItemFilter()

    data class CustomValueAtMost(val name: String, val value: Double) : ItemFilter()

    data class CustomValueEquals(val name: String, val value: String) : ItemFilter()
}

private fun parseNumericPrefix(raw: String): Double? = Regex("""^[\d.]+""").find(raw.trim())?.value?.toDoubleOrNull()

/**
 * Finds the first [ItemProperty.Custom] whose name matches [name], or `null` if absent.
 */
private fun Item.findCustom(name: String): ItemProperty.Custom? =
    properties
        .filterIsInstance<ItemProperty.Custom>()
        .firstOrNull { it.name == name }

/**
 * Returns `true` if this item satisfies **all** of the given [filters] (AND semantics).
 *
 * - **Empty list:** vacuous truth — an empty filter list always returns `true`.
 * - **Missing property:** strict exclusion — if a filter references a property that is absent
 *   from the item, that filter evaluates to `false` and the whole call returns `false`.
 * - **Numeric parsing:** custom property values are parsed with a leading-digit regex
 *   (`^[\d.]+`), which extracts the longest numeric prefix. Assumes positive values and
 *   uses `.` as the decimal separator. Unparseable values are treated as missing.
 */
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
                val custom = findCustom(filter.name) ?: return@all false
                val numeric = parseNumericPrefix(custom.value) ?: return@all false
                numeric >= filter.value
            }
            is ItemFilter.CustomValueAtMost -> {
                val custom = findCustom(filter.name) ?: return@all false
                val numeric = parseNumericPrefix(custom.value) ?: return@all false
                numeric <= filter.value
            }
            is ItemFilter.CustomValueEquals -> {
                val custom = findCustom(filter.name) ?: return@all false
                custom.value.equals(filter.value, ignoreCase = true)
            }
        }
    }
