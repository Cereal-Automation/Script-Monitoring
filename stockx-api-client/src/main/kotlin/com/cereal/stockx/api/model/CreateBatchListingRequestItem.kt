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
 * @param quantity The total number of Listings that need to be created.
 * @param variantId Unique StockX variant ID that this Listing is being created for
 * @param amount The amount this Listing is being listed for
 * @param active Flag used to indicate that the listing should be active or not
 * @param currencyCode The currency this Listing is being listed in. If not provided, it will default to USD.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
 * @param expiresAt UTC timestamp representing when this Listing should auto-expire.  If not provided, it will default to 365 days from today. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z
 */


data class CreateBatchListingRequestItem (

    /* The total number of Listings that need to be created. */
    @Json(name = "quantity")
    val quantity: kotlin.Double,

    /* Unique StockX variant ID that this Listing is being created for */
    @Json(name = "variantId")
    val variantId: kotlin.String,

    /* The amount this Listing is being listed for */
    @Json(name = "amount")
    val amount: kotlin.String,

    /* Flag used to indicate that the listing should be active or not */
    @Json(name = "active")
    val active: kotlin.Boolean? = null,

    /* The currency this Listing is being listed in. If not provided, it will default to USD.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\" */
    @Json(name = "currencyCode")
    val currencyCode: kotlin.String? = null,

    /* UTC timestamp representing when this Listing should auto-expire.  If not provided, it will default to 365 days from today. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z */
    @Json(name = "expiresAt")
    val expiresAt: kotlin.String? = null

) {


}

