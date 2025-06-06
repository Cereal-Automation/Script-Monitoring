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

import com.cereal.stockx.api.model.CreateBatchListingRequestItem

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param items An array of items that should be associated with the batch
 */


data class BatchCreateListingInput (

    /* An array of items that should be associated with the batch */
    @Json(name = "items")
    val items: kotlin.collections.List<CreateBatchListingRequestItem>

) {


}

