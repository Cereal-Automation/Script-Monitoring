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
 * @param askId Id of the created ask
 * @param askCreatedAt When the ask was created
 * @param askUpdatedAt When the ask was updated
 * @param askExpiresAt When the ask will expire
 */


data class ListingResponseAsk (

    /* Id of the created ask */
    @Json(name = "askId")
    val askId: kotlin.String,

    /* When the ask was created */
    @Json(name = "askCreatedAt")
    val askCreatedAt: kotlin.String,

    /* When the ask was updated */
    @Json(name = "askUpdatedAt")
    val askUpdatedAt: kotlin.String,

    /* When the ask will expire */
    @Json(name = "askExpiresAt")
    val askExpiresAt: kotlin.String? = null

) {


}

