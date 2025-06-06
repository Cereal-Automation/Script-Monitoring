package com.cereal.command.monitor.models

enum class Currency(
    val code: String,
    val symbol: String,
) {
    USD("USD", "$"),
    EUR("EUR", "€"),
    GBP("GBP", "£"),
    JPY("JPY", "¥"),
    INR("INR", "₹"),
    AUD("AUD", "A$"),
    CAD("CAD", "C$"),
    CNY("CNY", "¥"),
    CHF("CHF", "CHF"),
    ;

    companion object {
        fun fromCode(code: String): Currency? = entries.find { it.code == code }
    }
}
