package com.cereal.command.monitor.models

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

sealed class ItemProperty(
    val commonName: String,
) {
    /**
     * Represents the price of an item, including its numerical value and currency.
     *
     * @property value The monetary value of the price.
     * @property currency The currency in which the price is denominated.
     *
     * This class extends the [ItemProperty] base class, specifying the property name as "price".
     *
     * The `toString` method provides a string representation of the price formatted according
     * to the default locale, using the appropriate currency symbol.
     */
    data class Price(
        val value: BigDecimal,
        val currency: Currency,
    ) : ItemProperty("price") {
        override fun toString(): String {
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
            currencyFormatter.currency = java.util.Currency.getInstance(currency.code)
            return currencyFormatter.format(value)
        }
    }

    /**
     * The date at which the item was published.
     *
     * @param value null when publish date couldn't reliably be determined. For example when the user just started
     * monitoring items and there's no publish date information in the data retrieved from the external datasource.
     */
    data class PublishDate(
        val value: Instant?,
    ) : ItemProperty("publish date") {
        override fun toString(): String {
            val localDateTime = value?.toLocalDateTime(TimeZone.currentSystemDefault())?.toJavaLocalDateTime()
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            return localDateTime?.format(formatter) ?: "Unknown"
        }
    }

    /**
     * Represents the stock status of an item in an inventory or catalog.
     *
     * This class provides information about whether an item is in stock,
     * the quantity available, or the stock level as a descriptive value.
     *
     * @property isInStock Indicates whether the item is in stock.
     * @property amount The quantity of items in stock, if available.
     * @property level A descriptive value representing the stock level (e.g., "low", "high"), if the amount is not specified.
     */
    data class Stock(
        val isInStock: Boolean,
        val amount: Int?,
        val level: String?,
    ) : ItemProperty("stock") {
        override fun toString(): String =
            if (isInStock) {
                stockValue() ?: "yes"
            } else {
                "no"
            }

        fun stockValue(): String? = amount?.toString() ?: level
    }

//    /**
//     * Represents a collection of variants associated with an item property. Each variant contains specific
//     * characteristics and properties that define it, such as its unique name and associated attributes.
//     *
//     * The primary use of this class is to store and process a list of variants, providing a string representation
//     * that concisely summarizes all variants and their properties.
//     *
//     * @param value A list of Variant objects that describe different options or configurations
//     *              related to the item property.
//     */
//    data class Variants : ItemProperty("sizes") {
//        override fun toString(): String = value.joinToString("\n") { "${it.name}: ${it.properties.toDisplay()}" }
//    }

    /**
     * Represents a custom property associated with an item.
     *
     * This class extends the base `ItemProperty` class and allows defining a custom
     * property by specifying a name and its corresponding value. The custom property name
     * is also used as the common name inherited from `ItemProperty`.
     *
     * @constructor Creates a `Custom` property with a specified name and value.
     * @property name The name of the custom property.
     * @property value The value associated with the custom property.
     */
    data class Custom(
        val name: String,
        val value: String,
    ) : ItemProperty(name) {
        override fun toString(): String = value
    }
}

fun List<ItemProperty>.toDisplay(): String = joinToString(" / ") { "${it.commonName}: $it" }

inline fun <reified T : ItemProperty> Item.getValue(): T? = properties.filterIsInstance<T>().firstOrNull()

inline fun <reified T : ItemProperty> Item.requireValue(): T =
    getValue() ?: throw MissingValueTypeException(
        T::class,
    )

inline fun <reified T : ItemProperty> Variant.getValue(): T? = properties.filterIsInstance<T>().firstOrNull()

inline fun <reified T : ItemProperty> Variant.requireValue(): T =
    getValue() ?: throw MissingValueTypeException(
        T::class,
    )
