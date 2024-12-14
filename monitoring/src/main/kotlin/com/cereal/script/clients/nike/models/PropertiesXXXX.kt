package com.cereal.script.clients.nike.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PropertiesXXXX(
    @SerialName("coverCard")
    val coverCard: CoverCard = CoverCard(),
    @SerialName("custom")
    val custom: CustomXXX? = CustomXXX(),
    @SerialName("metadataDecorations")
    val metadataDecorations: List<MetadataDecoration>? = listOf(),
    @SerialName("products")
    val products: List<ProductX> = listOf(),
    @SerialName("publish")
    val publish: Publish = Publish(),
    @SerialName("seo")
    val seo: Seo = Seo(),
    @SerialName("social")
    val social: Social? = Social(),
    @SerialName("subtitle")
    val subtitle: String = "",
    @SerialName("threadType")
    val threadType: String = "",
    @SerialName("title")
    val title: String = ""
)