package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Props(
    @SerialName("__N_SSP")
    val nSSP: Boolean = false,
    @SerialName("pageProps")
    val pageProps: PageProps =
        PageProps(),
)
