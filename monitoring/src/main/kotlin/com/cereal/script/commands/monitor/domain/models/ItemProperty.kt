package com.cereal.script.commands.monitor.domain.models

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

sealed class ItemProperty(
    val commonName: String,
) {
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
            val localDateTime = LocalDateTime.ofInstant(value, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            return localDateTime.format(formatter)
        }
    }

    data class AvailableStock(
        val value: Int,
    ) : ItemProperty("stock") {
        override fun toString(): String = value.toString()
    }

    data class InStock(
        val value: Boolean,
    ) : ItemProperty("in stock") {
        override fun toString(): String = if (value) "yes" else "no"
    }

    data class Variants(
        val value: List<Variant>,
    ) : ItemProperty("sizes") {
        override fun toString(): String = value.joinToString("\n") { "${it.name}: ${it.stockLevel}" }
    }

    data class Custom(
        val name: String,
        val value: String,
    ) : ItemProperty(name) {
        override fun toString(): String = value
    }
}

inline fun <reified T : ItemProperty> Item.getValue(): T? = properties.filterIsInstance<T>().firstOrNull()

inline fun <reified T : ItemProperty> Item.requireValue(): T = getValue() ?: throw MissingValueTypeException(T::class)
