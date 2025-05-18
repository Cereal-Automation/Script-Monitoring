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
    ) : ItemProperty("release date") {
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

    /**
     * Represents the size information of an item.
     *
     * This class provides size details including the size value and its type.
     *
     * @property type The type of size classification.
     * @property value The actual size value.
     */
    data class Size(
        val type: SizeType,
        val value: SizeValue,
    ) : ItemProperty("size") {
        override fun toString(): String = "${value.display} (${type.displayName})"

        /**
         * Enumeration of size types used in various sizing systems.
         *
         * @property displayName The human-readable name of the size type.
         */
        enum class SizeType(val displayName: String) {
            US_MEN("US Men's"),
            US_WOMEN("US Women's"),
            US_YOUTH("US Youth"),
            US_INFANT("US Infant"),
            US_PRESCHOOL("US Preschool"),
            EU("EU"),
            UK("UK"),
            CM("CM"),
            JP("JP"),
            ONE_SIZE("One Size"),
            ;

            companion object {
                fun fromString(value: String): SizeType {
                    return when (value.uppercase()) {
                        "US MEN'S", "US MENS", "US MEN" -> US_MEN
                        "US WOMEN'S", "US WOMENS", "US WOMEN" -> US_WOMEN
                        "US YOUTH", "Y" -> US_YOUTH
                        "US INFANT", "INFANT" -> US_INFANT
                        "US PRESCHOOL", "PRESCHOOL", "PS" -> US_PRESCHOOL
                        "EU" -> EU
                        "UK" -> UK
                        "CM" -> CM
                        "JP" -> JP
                        "ONE SIZE", "OS" -> ONE_SIZE
                        else -> throw IllegalArgumentException("Unknown size type: $value")
                    }
                }
            }
        }

        /**
         * Enum representing standardized size values.
         *
         * @property display The display representation of the size value.
         */
        enum class SizeValue(val display: String) {
            // US Men's sizes
            US_MEN_4("4"),
            US_MEN_4_5("4.5"),
            US_MEN_5("5"),
            US_MEN_5_5("5.5"),
            US_MEN_6("6"),
            US_MEN_6_5("6.5"),
            US_MEN_7("7"),
            US_MEN_7_5("7.5"),
            US_MEN_8("8"),
            US_MEN_8_5("8.5"),
            US_MEN_9("9"),
            US_MEN_9_5("9.5"),
            US_MEN_10("10"),
            US_MEN_10_5("10.5"),
            US_MEN_11("11"),
            US_MEN_11_5("11.5"),
            US_MEN_12("12"),
            US_MEN_12_5("12.5"),
            US_MEN_13("13"),
            US_MEN_13_5("13.5"),
            US_MEN_14("14"),
            US_MEN_15("15"),
            US_MEN_16("16"),
            US_MEN_17("17"),
            US_MEN_18("18"),

            // US Women's sizes
            US_WOMEN_5("5"),
            US_WOMEN_5_5("5.5"),
            US_WOMEN_6("6"),
            US_WOMEN_6_5("6.5"),
            US_WOMEN_7("7"),
            US_WOMEN_7_5("7.5"),
            US_WOMEN_8("8"),
            US_WOMEN_8_5("8.5"),
            US_WOMEN_9("9"),
            US_WOMEN_9_5("9.5"),
            US_WOMEN_10("10"),
            US_WOMEN_10_5("10.5"),
            US_WOMEN_11("11"),
            US_WOMEN_11_5("11.5"),
            US_WOMEN_12("12"),

            // EU sizes
            EU_35("35"),
            EU_36("36"),
            EU_37("37"),
            EU_38("38"),
            EU_39("39"),
            EU_40("40"),
            EU_41("41"),
            EU_42("42"),
            EU_43("43"),
            EU_44("44"),
            EU_45("45"),
            EU_46("46"),
            EU_47("47"),
            EU_48("48"),
            EU_49("49"),
            EU_50("50"),

            // UK sizes
            UK_3("3"),
            UK_3_5("3.5"),
            UK_4("4"),
            UK_4_5("4.5"),
            UK_5("5"),
            UK_5_5("5.5"),
            UK_6("6"),
            UK_6_5("6.5"),
            UK_7("7"),
            UK_7_5("7.5"),
            UK_8("8"),
            UK_8_5("8.5"),
            UK_9("9"),
            UK_9_5("9.5"),
            UK_10("10"),
            UK_10_5("10.5"),
            UK_11("11"),
            UK_11_5("11.5"),
            UK_12("12"),

            // Other sizes
            ONE_SIZE("One Size"),
            ;

            companion object {
                fun fromString(value: String): SizeValue {
                    return SizeValue.entries.find { it.display == value }
                        ?: throw IllegalArgumentException("Unknown size value: $value")
                }
            }
        }
    }

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
