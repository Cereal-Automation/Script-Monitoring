
package com.cereal.command.monitor.data.tgtg.apiclients.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataDomeCookieResponse(
    @SerialName("status")
    val status: Int? = null,
    @SerialName("cookie")
    val cookie: String? = null,
)
