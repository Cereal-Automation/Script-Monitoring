package com.cereal.script.clients.snkrs.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Collectionsv2(
    @SerialName("groupedCollectionTermIds")
    val groupedCollectionTermIds: GroupedCollectionTermIds? = null,
)
