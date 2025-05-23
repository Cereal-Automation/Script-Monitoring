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

import com.cereal.stockx.api.model.OperationApi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param nextCursor Used for pagination when more than 200 operations are retrieved. The nextCursor value received in response will be same for all subsequent page requests.
 * @param operations A list of the operations requested
 */


data class OperationsCursorResponse (

    /* Used for pagination when more than 200 operations are retrieved. The nextCursor value received in response will be same for all subsequent page requests. */
    @Json(name = "nextCursor")
    val nextCursor: kotlin.String?,

    /* A list of the operations requested */
    @Json(name = "operations")
    val operations: kotlin.collections.List<OperationApi>

) {


}

