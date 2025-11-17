package com.cereal.command.monitor.data.bolcom.httpclient.model

import kotlinx.serialization.Serializable

@Serializable
data class BolcomSearchResponse(
    val customer: Customer? = null,
) {
    @Serializable
    data class Customer(
        val basket: Basket? = null,
    )

    @Serializable
    data class Basket(
        val totalQuantity: TotalQuantity? = null,
    )

    @Serializable
    data class TotalQuantity(
        @kotlinx.serialization.SerialName("routes/searchPage")
        val routesSearchPage: SearchPage? = null,
    ) {
        @Serializable
        data class SearchPage(
            val data: SearchPageData? = null,
        )
    }

    @Serializable
    data class SearchPageData(
        val template: Template? = null,
    )

    @Serializable
    data class Template(
        val regions: List<Region> = emptyList(),
    )

    @Serializable
    data class Region(
        val slots: List<Slot> = emptyList(),
    )

    @Serializable
    data class Slot(
        val type: String? = null,
        val props: SlotProps? = null,
    )

    @Serializable
    data class SlotProps(
        val product: Product? = null,
    )

    @Serializable
    data class Product(
        val id: String? = null,
        val title: String? = null,
        val url: String? = null,
        val relatedParties: List<RelatedParty> = emptyList(),
        val assets: List<Asset> = emptyList(),
        val attributes: List<Attribute> = emptyList(),
        val bestSellingOffer: BestSellingOffer? = null,
    )

    @Serializable
    data class RelatedParty(
        val role: String? = null,
        val party: Party? = null,
    )

    @Serializable
    data class Party(
        val name: String? = null,
    )

    @Serializable
    data class Asset(
        val renditions: List<Rendition> = emptyList(),
    )

    @Serializable
    data class Rendition(
        val url: String? = null,
    )

    @Serializable
    data class Attribute(
        val name: String? = null,
        val values: List<AttributeValue> = emptyList(),
    )

    @Serializable
    data class AttributeValue(
        val value: String? = null,
    )

    @Serializable
    data class BestSellingOffer(
        val sellingPrice: SellingPrice? = null,
        val sellingPriceDiscountOnStrikethroughPrice: DiscountPrice? = null,
        val strikethroughPrice: StrikethroughPrice? = null,
        val retailer: Retailer? = null,
        val bestDeliveryOption: DeliveryOption? = null,
        val deliveredWithin48Hours: Boolean? = null,
    )

    @Serializable
    data class SellingPrice(
        val price: Price? = null,
    )

    @Serializable
    data class Price(
        val amount: Double? = null,
    )

    @Serializable
    data class DiscountPrice(
        val amount: DiscountAmount? = null,
    )

    @Serializable
    data class DiscountAmount(
        val amount: Double? = null,
    )

    @Serializable
    data class StrikethroughPrice(
        val price: Price? = null,
    )

    @Serializable
    data class Retailer(
        val name: String? = null,
        val id: String? = null,
    )

    @Serializable
    data class DeliveryOption(
        val deliveryDescription: String? = null,
    )
}
