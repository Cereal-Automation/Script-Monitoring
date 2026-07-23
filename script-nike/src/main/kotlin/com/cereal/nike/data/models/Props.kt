package com.cereal.nike.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Props(
    @SerialName("pageProps")
    val pageProps: PageProps =
        PageProps(),
)
