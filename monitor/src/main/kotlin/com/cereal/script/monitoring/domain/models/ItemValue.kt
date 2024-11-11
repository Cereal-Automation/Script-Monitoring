package com.cereal.script.monitoring.domain.models

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

sealed class ItemValue(
    val commonName: String,
) {
    data class Price(
        val value: BigDecimal,
        val currency: Currency,
    ) : ItemValue("price") {
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
    ) : ItemValue("publish date") {
        override fun toString(): String {
            val localDateTime = LocalDateTime.ofInstant(value, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            return localDateTime.format(formatter)
        }
    }

    data class AvailableStock(
        val value: Int,
    ) : ItemValue("stock") {
        override fun toString(): String = value.toString()
    }

    data class InStock(
        val value: Boolean,
    ) : ItemValue("in stock") {
        override fun toString(): String = if (value) "yes" else "no"
    }
}

inline fun <reified T : ItemValue> Item.getValue(): T? = values.filterIsInstance<T>().firstOrNull()

inline fun <reified T : ItemValue> Item.requireValue(): T = getValue() ?: throw MissingValueTypeException(T::class)
