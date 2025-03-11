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
    data class Price(
        val value: BigDecimal,
        val currency: com.cereal.command.monitor.models.Currency,
    ) : com.cereal.command.monitor.models.ItemProperty("price") {
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
    ) : com.cereal.command.monitor.models.ItemProperty("publish date") {
        override fun toString(): String {
            val localDateTime = value?.toLocalDateTime(TimeZone.currentSystemDefault())?.toJavaLocalDateTime()
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            return localDateTime?.format(formatter) ?: "Unknown"
        }
    }

    data class AvailableStock(
        val value: Int,
    ) : com.cereal.command.monitor.models.ItemProperty("stock") {
        override fun toString(): String = value.toString()
    }

    data class InStock(
        val value: Boolean,
    ) : com.cereal.command.monitor.models.ItemProperty("in stock") {
        override fun toString(): String = if (value) "yes" else "no"
    }

    data class Variants(
        val value: List<com.cereal.command.monitor.models.Variant>,
    ) : com.cereal.command.monitor.models.ItemProperty("sizes") {
        override fun toString(): String = value.joinToString("\n") { "${it.name}: ${it.stockLevel}" }
    }

    data class Custom(
        val name: String,
        val value: String,
    ) : com.cereal.command.monitor.models.ItemProperty(name) {
        override fun toString(): String = value
    }
}

inline fun <reified T : com.cereal.command.monitor.models.ItemProperty> com.cereal.command.monitor.models.Item.getValue(): T? =
    properties.filterIsInstance<T>().firstOrNull()

inline fun <reified T : com.cereal.command.monitor.models.ItemProperty> com.cereal.command.monitor.models.Item.requireValue(): T =
    getValue() ?: throw com.cereal.command.monitor.models.MissingValueTypeException(
        T::class,
    )
