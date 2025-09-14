package com.cereal.command.monitor.models

/**
 * Represents the criteria used for searching items.
 *
 * @property styleId The unique identifier for the item's style or variant.
 * @property currency The desired currency for filtering or matching item-related information.
 *
 * This data class is utilized in scenarios where filtering or searching for specific items
 * is required, primarily through attributes such as the style ID and currency.
 */
data class SearchCriteria(
    val styleId: String,
    val currency: Currency,
)
