package com.cereal.script.monitoring.domain.models

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

sealed class ItemValue(val commonName: String) {
    data class Price(val value: BigDecimal, val currency: Currency): ItemValue("price") {
        override fun toString(): String {
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
            currencyFormatter.currency = java.util.Currency.getInstance(currency.code)
            return currencyFormatter.format(value)
        }
    }

    /**
     * The date at which the item was published.
     */
    data class PublishDate(val value: Instant): ItemValue("publish date") {
        override fun toString(): String {
            val localDateTime = LocalDateTime.ofInstant(value, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            return localDateTime.format(formatter)
        }
    }

    data class Stock(val value: Int): ItemValue("stock") {
        override fun toString(): String {
            return value.toString()
        }
    }
}

inline fun <reified T : ItemValue> Item.getValue(): T? {
    return values.filterIsInstance<T>().firstOrNull()
}

inline fun <reified T : ItemValue> Item.requireValue(): T {
    return getValue() ?: throw MissingValueTypeException(T::class)
}
