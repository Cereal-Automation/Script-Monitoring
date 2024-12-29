package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Props(
    @SerialName("pageProps")
    val pageProps: PageProps =
        PageProps(),
)
