@file:OptIn(ExperimentalSerializationApi::class)

package com.cereal.command.monitor.data.bolcom.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/*
 * Serializable mapping of the *product-related* part of a bol.com search response
 * (bol_response.json). Only product info is modelled: name, url, ids, description,
 * images, categories, brand/parties, reviews, specifications and everything around
 * pricing & discounts (selling offers, price, select/unit price, promotional labels,
 * delivery, retailer, condition, refurbished/second-hand/return-deal offers).
 *
 * Non-product structures (template, facets, textResources, labels, consent, ...) are
 * intentionally not modelled and are skipped via `ignoreUnknownKeys`.
 *
 * Notes about this particular dump:
 *  - Absent object/number fields are encoded with the sentinel numbers -5 (and -7).
 *    `NullableSellingOfferSerializer` / `SentinelElementSerializer` decode those to null.
 *  - A few scalars in the dump are "poisoned" with large nested blobs (asset.order,
 *    retailer.id, reviews.total, rendition.width/height). Those keys are simply not
 *    mapped, so they are ignored.
 *  - The dump ends with a trailing comma; `allowTrailingComma` handles that.
 *  - `promotionalLabels[].titleText` is a campaign/cross-sell banner (e.g. "15% korting
 *    op ASUS WiFi", "deal"), NOT the product's own discount — don't use it for pricing.
 *    The real discount lives under `bestSellingOffer.savings`.
 *  - `bestDeliveryOption.deliveryDescription` is display text mixing stock + shipping
 *    timing (e.g. "Op voorraad. Voor 23:59 uur besteld, morgen in huis") and isn't
 *    reliable to parse for availability. Real availability is derived from whether a
 *    selling offer exists, `isScarce`, and `bestDeliveryOption.productReleaseDate`.
 *
 * Usage:
 *   val products = parseBolProducts(text)
 */

@Serializable
data class BolProduct(
    val id: String,
    val title: String,
    val url: String,
    val explicit: Boolean = false,
    val offerComparePageUrl: String? = null,
    val family: ProductFamily? = null,
    val classification: ProductClassification? = null,
    val categories: List<ProductCategory> = emptyList(),
    val relatedParties: List<ProductPartyRelation> = emptyList(),
    val reviews: ProductReviews? = null,
    val assets: List<Asset> = emptyList(),
    val attributes: List<ProductAttribute> = emptyList(),
    val specifications: ProductSpecifications? = null,
    val showProductQuantityOptions: Boolean = false,
    val availabilityNotificationRegistered: Boolean = false,
    // ---- Pricing / offers -------------------------------------------------
    @Serializable(with = NullableSellingOfferSerializer::class)
    val bestSellingOffer: SellingOffer? = null,
    @Serializable(with = NullableSellingOfferSerializer::class)
    val sellingOfferInConditionNewWithCheapestPrice: SellingOffer? = null,
    @Serializable(with = NullableSellingOfferSerializer::class)
    val sellingOfferInConditionSecondhandWithCheapestPrice: SellingOffer? = null,
    @Serializable(with = NullableSellingOfferSerializer::class)
    val cheapestRefurbishedSellingOffer: SellingOffer? = null,
    @Serializable(with = NullableSellingOfferSerializer::class)
    val bestReturnDealSellingOffer: SellingOffer? = null,
    val sellingOffersInConditionRefurbished: SellingOfferConnection? = null,
) {
    /** HTML product description (from the "Description" attribute). */
    val description: String?
        get() =
            attributes.firstOrNull { it.name.equals("Description", ignoreCase = true) }
                ?.values?.firstOrNull()?.value

    /** URL of the primary product image (largest available rendition). */
    val primaryImageUrl: String?
        get() =
            (assets.firstOrNull { it.usage == "primary" } ?: assets.firstOrNull())
                ?.renditions?.firstOrNull()?.url

    /** Current selling price amount, e.g. "899.00". */
    val price: String?
        get() = bestSellingOffer?.sellingPrice?.price?.amount

    /** Reference / "most-shown" price the discount is measured against, if discounted. */
    val referencePrice: String?
        get() = bestSellingOffer?.referencePrice

    /** Discount percentage on the best offer, if discounted (e.g. 13). */
    val discountPercentage: Int?
        get() = bestSellingOffer?.discountPercentage

    /** Brand name, e.g. "Sony Playstation". */
    val brandName: String?
        get() = relatedParties.firstOrNull { it.role == "BRAND" }?.party?.name

    val averageRating: Double?
        get() = reviews?.averageRating
}

@Serializable
data class ProductFamily(
    val id: String? = null,
)

@Serializable
data class ProductClassification(
    val brickId: String? = null,
    val chunkId: String? = null,
)

@Serializable
data class ProductCategory(
    val name: String,
    val parents: List<ProductCategory> = emptyList(),
)

@Serializable
data class ProductPartyRelation(
    // BRAND | PUBLISHER | CHARACTER
    val role: String,
    val party: Party,
)

@Serializable
data class Party(
    val id: String? = null,
    val name: String,
    val url: String? = null,
)

@Serializable
data class ProductReviews(
    val averageRating: Double? = null,
    // `total` (review count) is poisoned in this dump and therefore not mapped.
)

@Serializable
data class Asset(
    val id: String? = null,
    // e.g. IMAGE
    val mediaType: String? = null,
    // e.g. primary
    val usage: String? = null,
    val renditions: List<AssetRendition> = emptyList(),
    // `order` is poisoned in this dump and therefore not mapped.
)

@Serializable
data class AssetRendition(
    val url: String,
    val mimeType: String? = null,
    // e.g. large
    val preset: String? = null,
    // width/height are poisoned in this dump and therefore not mapped.
)

@Serializable
data class ProductAttribute(
    val name: String,
    val values: List<ProductAttributeValue> = emptyList(),
)

@Serializable
data class ProductAttributeValue(
    val value: String,
)

@Serializable
data class ProductSpecifications(
    val detailedSummary: ProductSpecificationSummary? = null,
)

@Serializable
data class ProductSpecificationSummary(
    val attributes: List<ProductSpecificationAttribute> = emptyList(),
)

@Serializable
data class ProductSpecificationAttribute(
    val textValues: List<String> = emptyList(),
)

// ---- Offers / pricing / discounts ----------------------------------------

@Serializable
data class SellingOffer(
    val offerUid: String? = null,
    // e.g. STANDARD
    val offerType: String? = null,
    val revisionId: String? = null,
    val condition: OfferCondition? = null,
    val deliveredWithin48Hours: Boolean = false,
    val isScarce: Boolean = false,
    val selectLabel: Boolean = false,
    val sellingPrice: SellingPrice? = null,
    val bestDeliveryOption: BestDeliveryOption? = null,
    val promotionalLabels: List<DiscountLabel> = emptyList(),
    @Serializable(with = RetailActionListSerializer::class)
    val retailActions: List<RetailAction> = emptyList(),
    val retailer: Retailer? = null,
    // Present only on discounted offers; the -5 sentinel maps to null.
    @Serializable(with = NullableSavingsSerializer::class) val savings: SellingOfferSavings? = null,
    // Shapes not seen populated in the samples; kept as raw JSON for forward-compat.
    @Serializable(with = SentinelElementSerializer::class) val selectPrice: JsonElement? = null,
    @Serializable(with = SentinelElementSerializer::class) val unitPrice: JsonElement? = null,
    @Serializable(with = SentinelElementSerializer::class) val bundlePrices: JsonElement? = null,
) {
    /** Current selling price, e.g. "30.42". */
    val amount: String? get() = sellingPrice?.price?.amount

    /** Reference / "most-shown" price the discount is measured against, e.g. "34.99". */
    val referencePrice: String? get() = savings?.reference?.referencePrice?.amount

    /** Discount percentage vs. the reference price (non-Select), e.g. 13. */
    val discountPercentage: Int? get() = savings?.reference?.sellingPriceDiscount?.percentage

    /** Discount amount in currency vs. the reference price, e.g. "4.57". */
    val discountAmount: String? get() = savings?.reference?.sellingPriceDiscount?.amount?.amount

    val hasDiscount: Boolean get() = savings?.reference?.sellingPriceDiscount != null
}

@Serializable
data class OfferCondition(
    // OfferConditionNew | OfferConditionRefurbished | OfferConditionSecondhand
    @SerialName("__typename") val type: String? = null,
)

@Serializable
data class SellingPrice(
    val price: Money? = null,
)

@Serializable
data class Money(
    // The API returns this as a string ("30.42") in sellingPrice but a number
    // (34.99) inside savings, so decode both shapes into a String.
    @Serializable(with = StringOrNumberSerializer::class) val amount: String? = null,
)

/** Discount / savings info; present on the offer only when the product is discounted. */
@Serializable
data class SellingOfferSavings(
    val reference: SellingOfferSavingsReference? = null,
    @Serializable(with = NullableDiscountSerializer::class)
    val selectPriceDiscountOnSellingPrice: Discount? = null,
)

@Serializable
data class SellingOfferSavingsReference(
    // "most-shown" price (shown struck through)
    val referencePrice: Money? = null,
    val strikethrough: Boolean = false,
    // discount vs. the reference price
    @Serializable(with = NullableDiscountSerializer::class)
    val sellingPriceDiscount: Discount? = null,
    // extra discount for Select members
    @Serializable(with = NullableDiscountSerializer::class)
    val selectPriceDiscount: Discount? = null,
    val savingsText: LocalizedText? = null,
    val infoLink: ReferencePriceInfoLink? = null,
    val text: ReferencePriceText? = null,
)

@Serializable
data class Discount(
    // discount in currency, e.g. "4.57"
    val amount: Money? = null,
    // discount percent, e.g. 13
    val percentage: Int? = null,
)

@Serializable
data class LocalizedText(
    val key: String? = null,
    val text: String? = null,
)

@Serializable
data class ReferencePriceInfoLink(
    val text: LocalizedText? = null,
    val url: String? = null,
)

@Serializable
data class ReferencePriceText(
    // e.g. "Meestal"
    val shortText: LocalizedText? = null,
    val description: LocalizedText? = null,
    val screenReaderText: LocalizedText? = null,
)

@Serializable
data class BestDeliveryOption(
    val deliveryDescription: String? = null,
    // ISO date
    val maxDateAtCustomer: String? = null,
    // ISO date; non-null means the product is a pre-order
    val productReleaseDate: String? = null,
)

@Serializable
data class DiscountLabel(
    // campaign/cross-sell banner text, e.g. "deal" — not a discount value
    val titleText: String? = null,
    val link: PromotionalLabelLink? = null,
    val retailAction: RetailAction? = null,
)

@Serializable
data class PromotionalLabelLink(
    val text: String? = null,
    val url: String? = null,
    val promoParameter: String? = null,
)

@Serializable
data class RetailAction(
    val id: String? = null,
)

@Serializable
data class Retailer(
    val name: String? = null,
    // `id` is poisoned in this dump and therefore not mapped.
)

@Serializable
data class SellingOfferConnection(
    val edges: List<SellingOfferEdge> = emptyList(),
)

@Serializable
data class SellingOfferEdge(
    @Serializable(with = NullableSellingOfferSerializer::class) val node: SellingOffer? = null,
)

// ---- Sentinel-tolerant serializers ---------------------------------------

/**
 * Decodes an object of type [T], or null when the value is absent / a sentinel
 * (e.g. the number -5). Real objects are decoded normally with the active [Json] config.
 */
abstract class SentinelNullableSerializer<T : Any>(
    private val delegate: KSerializer<T>,
) : KSerializer<T?> {
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): T? {
        val json = decoder as? JsonDecoder ?: return delegate.deserialize(decoder)
        val element = json.decodeJsonElement()
        return if (element is JsonObject) json.json.decodeFromJsonElement(delegate, element) else null
    }

    override fun serialize(
        encoder: Encoder,
        value: T?,
    ) {
        if (value == null) encoder.encodeNull() else delegate.serialize(encoder, value)
    }
}

object NullableSellingOfferSerializer :
    SentinelNullableSerializer<SellingOffer>(SellingOffer.serializer())

object NullableSavingsSerializer :
    SentinelNullableSerializer<SellingOfferSavings>(SellingOfferSavings.serializer())

object NullableDiscountSerializer :
    SentinelNullableSerializer<Discount>(Discount.serializer())

/** Decodes a JSON string ("30.42") or number (34.99) into a String. */
object StringOrNumberSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StringOrNumber", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String? {
        val json = decoder as? JsonDecoder ?: return decoder.decodeString()
        val element = json.decodeJsonElement()
        if (element is JsonNull) return null
        return (element as? JsonPrimitive)?.content
    }

    override fun serialize(
        encoder: Encoder,
        value: String?,
    ) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value)
    }
}

/**
 * Keeps any real JSON value, but maps JSON null and the -5 / -7 numeric sentinels to null.
 * Used for fields whose populated shape is not present in this dump.
 */
object SentinelElementSerializer : KSerializer<JsonElement?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("SentinelElement", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): JsonElement? {
        val json = decoder as? JsonDecoder ?: return null
        val element = json.decodeJsonElement()
        if (element is JsonNull) return null
        if (element is JsonPrimitive && !element.isString && element.content in SENTINELS) return null
        return element
    }

    override fun serialize(
        encoder: Encoder,
        value: JsonElement?,
    ) {
        val json =
            encoder as? JsonEncoder
                ?: throw SerializationException("SentinelElementSerializer supports JSON only")
        json.encodeJsonElement(value ?: JsonNull)
    }

    private val SENTINELS = setOf("-5", "-7")
}

/**
 * Decodes a JSON array normally, but maps the -5 / -7 numeric sentinels (in place of an
 * empty/absent array) to an empty list, instead of failing with a decoding error.
 */
private class SentinelListSerializer<T>(elementSerializer: KSerializer<T>) : KSerializer<List<T>> {
    private val delegate = ListSerializer(elementSerializer)
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): List<T> {
        val json = decoder as? JsonDecoder ?: return delegate.deserialize(decoder)
        val element = json.decodeJsonElement()
        return if (element is JsonArray) json.json.decodeFromJsonElement(delegate, element) else emptyList()
    }

    override fun serialize(
        encoder: Encoder,
        value: List<T>,
    ) = delegate.serialize(encoder, value)
}

object RetailActionListSerializer : KSerializer<List<RetailAction>> by SentinelListSerializer(RetailAction.serializer())

// ---- Ready-to-use Json + helper ------------------------------------------

val BolJson: Json =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
        allowTrailingComma = true
    }

/**
 * Parses the top-level `products` array, decoding each product independently so that one
 * malformed entry (e.g. missing a required field) is skipped instead of failing the batch.
 */
fun parseBolProducts(jsonText: String): List<BolProduct> = parseBolProducts(BolJson.parseToJsonElement(jsonText))

/**
 * Same as [parseBolProducts], but for an already-parsed [JsonElement] tree
 * (e.g. from `Json.parseToJsonElement(text)`).
 */
fun parseBolProducts(element: JsonElement): List<BolProduct> {
    val productsArray = (element as? JsonObject)?.get("products") as? JsonArray ?: return emptyList()
    return productsArray.mapNotNull { runCatching { parseBolProduct(it) }.getOrNull() }
}

/** Decode a single product node (one `Product` object) from a [JsonElement]. */
fun parseBolProduct(element: JsonElement): BolProduct = BolJson.decodeFromJsonElement(BolProduct.serializer(), element)
