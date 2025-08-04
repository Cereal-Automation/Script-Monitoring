package com.cereal.command.monitor.data.zalando

import com.cereal.command.monitor.models.Currency

enum class ZalandoWebsite(
    val countryCode: String,
    val countryName: String,
    val url: String,
    val defaultCurrency: Currency,
) {
    UK(countryCode = "UK", countryName = "United Kingdom", url = "https://www.zalando.co.uk", defaultCurrency = Currency.GBP),
    NL(countryCode = "NL", countryName = "Netherlands", url = "https://www.zalando.nl", defaultCurrency = Currency.EUR),
    ;

    override fun toString(): String = countryName
}
