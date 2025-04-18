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

import com.cereal.stockx.api.model.AuthenticationDetails
import com.cereal.stockx.api.model.InventoryType
import com.cereal.stockx.api.model.ListOrdersStatus
import com.cereal.stockx.api.model.ManifestDataResponse
import com.cereal.stockx.api.model.OrderProduct
import com.cereal.stockx.api.model.Payout
import com.cereal.stockx.api.model.Shipment
import com.cereal.stockx.api.model.Variant

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param askId Unique identifier for an ask on the StockX platform
 * @param orderNumber The unique order number. Standard example: 323314425-323214184. Flex example: 02-L0QT6MRVSG
 * @param listingId Unique ID for this listing
 * @param amount The ask/order price
 * @param currencyCode The currency type for this order.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
 * @param createdAt When the order was created in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z
 * @param updatedAt When the order was updated in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z
 * @param variant 
 * @param product 
 * @param status 
 * @param shipment The shipment details of the order.
 * @param initiatedShipments An object containing details about the seller initiated shipments.
 * @param inventoryType 
 * @param payout 
 * @param authenticationDetails Details about authentication status and failure notes
 */


data class DetailedOrder (

    /* Unique identifier for an ask on the StockX platform */
    @Json(name = "askId")
    val askId: kotlin.String,

    /* The unique order number. Standard example: 323314425-323214184. Flex example: 02-L0QT6MRVSG */
    @Json(name = "orderNumber")
    val orderNumber: kotlin.String,

    /* Unique ID for this listing */
    @Json(name = "listingId")
    val listingId: kotlin.String?,

    /* The ask/order price */
    @Json(name = "amount")
    val amount: kotlin.String,

    /* The currency type for this order.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\" */
    @Json(name = "currencyCode")
    val currencyCode: kotlin.String?,

    /* When the order was created in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z */
    @Json(name = "createdAt")
    val createdAt: java.time.OffsetDateTime?,

    /* When the order was updated in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z */
    @Json(name = "updatedAt")
    val updatedAt: java.time.OffsetDateTime?,

    @Json(name = "variant")
    val variant: Variant,

    @Json(name = "product")
    val product: OrderProduct,

    @Json(name = "status")
    val status: ListOrdersStatus,

    /* The shipment details of the order. */
    @Json(name = "shipment")
    val shipment: Shipment?,

    /* An object containing details about the seller initiated shipments. */
    @Json(name = "initiatedShipments")
    val initiatedShipments: ManifestDataResponse?,

    @Json(name = "inventoryType")
    val inventoryType: InventoryType,

    @Json(name = "payout")
    val payout: Payout,

    /* Details about authentication status and failure notes */
    @Json(name = "authenticationDetails")
    val authenticationDetails: AuthenticationDetails? = null

) {


}

