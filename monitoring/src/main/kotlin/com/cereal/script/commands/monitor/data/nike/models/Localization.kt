package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Localization(
    @SerialName("cloudUrlFragment")
    val cloudUrlFragment: String = "",
    @SerialName("country")
    val country: String = "",
    @SerialName("countryName")
    val countryName: String = "",
    @SerialName("countryNames")
    val countryNames: CountryNames =
        CountryNames(),
    @SerialName("currency")
    val currency: String = "",
    @SerialName("currencySymbol")
    val currencySymbol: String = "",
    @SerialName("default")
    val default: Boolean = false,
    @SerialName("hreflang")
    val hreflang: String = "",
    @SerialName("intl")
    val intl: String = "",
    @SerialName("langRegion")
    val langRegion: String = "",
    @SerialName("language")
    val language: String = "",
    @SerialName("merchGroup")
    val merchGroup: String = "",
    @SerialName("messages")
    val messages: Messages =
        Messages(),
    @SerialName("reactIntl")
    val reactIntl: String = "",
    @SerialName("translationsLanguage")
    val translationsLanguage: String = "",
    @SerialName("urlParam")
    val urlParam: String = "",
)
