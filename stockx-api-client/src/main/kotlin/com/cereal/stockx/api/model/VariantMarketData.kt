/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.cereal.stockx.api.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param productId Unique identifier for this product
 * @param variantId Unique identifier for this product variant
 * @param currencyCode The currency code. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
 * @param highestBidAmount The highest bid for the product variant listed in the country requested.
 * @param lowestAskAmount The lowest ask for the product variant listed in the country requested.
 * @param sellFasterAmount The price you have to list at, inclusive of duties and taxes, to become the lowest Ask to buyers in the United States.
 * @param earnMoreAmount The price you have to list at, to become the lowest ask to buyers in your region. This accounts for VAT and taxes.
 * @param flexLowestAskAmount The Flex program's lowest ask for the product variant listed in the country requested.
 */


data class VariantMarketData (

    /* Unique identifier for this product */
    @Json(name = "productId")
    val productId: kotlin.String,

    /* Unique identifier for this product variant */
    @Json(name = "variantId")
    val variantId: kotlin.String,

    /* The currency code. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\" */
    @Json(name = "currencyCode")
    val currencyCode: kotlin.String,

    /* The highest bid for the product variant listed in the country requested. */
    @Json(name = "highestBidAmount")
    val highestBidAmount: kotlin.String?,

    /* The lowest ask for the product variant listed in the country requested. */
    @Json(name = "lowestAskAmount")
    val lowestAskAmount: kotlin.String? = null,

    /* The price you have to list at, inclusive of duties and taxes, to become the lowest Ask to buyers in the United States. */
    @Json(name = "sellFasterAmount")
    val sellFasterAmount: kotlin.String? = null,

    /* The price you have to list at, to become the lowest ask to buyers in your region. This accounts for VAT and taxes. */
    @Json(name = "earnMoreAmount")
    val earnMoreAmount: kotlin.String? = null,

    /* The Flex program's lowest ask for the product variant listed in the country requested. */
    @Json(name = "flexLowestAskAmount")
    val flexLowestAskAmount: kotlin.String? = null

) {


}

