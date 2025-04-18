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
 * JSON metadata object which include the changes of current listing
 *
 * @param additions The additions of current listing
 * @param updates The updates of current listing
 * @param removals The removals of current listing
 */


data class DiffRecursivePartialListingChangeableFields (

    /* The additions of current listing */
    @Json(name = "additions")
    val additions: kotlin.Any,

    /* The updates of current listing */
    @Json(name = "updates")
    val updates: kotlin.Any,

    /* The removals of current listing */
    @Json(name = "removals")
    val removals: kotlin.Any

) {


}

