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

import com.cereal.stockx.api.model.DiffRecursivePartialListingChangeableFields
import com.cereal.stockx.api.model.OperationInitiatedBy
import com.cereal.stockx.api.model.OperationStatus
import com.cereal.stockx.api.model.OperationType
import com.cereal.stockx.api.model.SupportedOperationInitiatedVia

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param listingId Unique ID for this listing
 * @param operationId Unique ID for this operation
 * @param operationType 
 * @param operationStatus 
 * @param operationUrl The URL used for to poll the status of the operation.
 * @param operationInitiatedBy 
 * @param operationInitiatedVia 
 * @param createdAt When the listing was created in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z
 * @param updatedAt When this listing was last updated in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z
 * @param changes 
 * @param error Error message if the creation failed
 */


data class ListingAsyncOperationResponse (

    /* Unique ID for this listing */
    @Json(name = "listingId")
    val listingId: kotlin.String,

    /* Unique ID for this operation */
    @Json(name = "operationId")
    val operationId: kotlin.String,

    @Json(name = "operationType")
    val operationType: OperationType,

    @Json(name = "operationStatus")
    val operationStatus: OperationStatus,

    /* The URL used for to poll the status of the operation. */
    @Json(name = "operationUrl")
    val operationUrl: kotlin.String?,

    @Json(name = "operationInitiatedBy")
    val operationInitiatedBy: OperationInitiatedBy,

    @Json(name = "operationInitiatedVia")
    val operationInitiatedVia: SupportedOperationInitiatedVia,

    /* When the listing was created in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z */
    @Json(name = "createdAt")
    val createdAt: kotlin.String,

    /* When this listing was last updated in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z */
    @Json(name = "updatedAt")
    val updatedAt: kotlin.String,

    @Json(name = "changes")
    val changes: DiffRecursivePartialListingChangeableFields,

    /* Error message if the creation failed */
    @Json(name = "error")
    val error: kotlin.String? = null

) {


}

