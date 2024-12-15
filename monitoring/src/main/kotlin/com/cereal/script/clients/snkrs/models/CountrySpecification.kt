package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountrySpecification(
    @SerialName("country")
    val country: String = "",
    @SerialName("localizedSize")
    val localizedSize: String = "",
    @SerialName("localizedSizePrefix")
    val localizedSizePrefix: String? = null,
    @SerialName("taxInfo")
    val taxInfo: TaxInfo? = null,
)
