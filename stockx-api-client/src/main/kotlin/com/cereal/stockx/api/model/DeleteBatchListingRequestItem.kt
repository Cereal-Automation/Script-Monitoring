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
 * @param listingId Unique listing ID that needs to be deleted
 */


data class DeleteBatchListingRequestItem (

    /* Unique listing ID that needs to be deleted */
    @Json(name = "listingId")
    val listingId: kotlin.String

) {


}

