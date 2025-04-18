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

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

import com.cereal.stockx.api.model.DetailedOrder
import com.cereal.stockx.api.model.AuthenticationDetails
import com.cereal.stockx.api.model.InventoryType
import com.cereal.stockx.api.model.ListOrdersStatus
import com.cereal.stockx.api.model.ManifestDataResponse
import com.cereal.stockx.api.model.OrderProduct
import com.cereal.stockx.api.model.Payout
import com.cereal.stockx.api.model.Shipment
import com.cereal.stockx.api.model.Variant

class DetailedOrderTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of DetailedOrder
        //val modelInstance = DetailedOrder()

        // to test the property `askId` - Unique identifier for an ask on the StockX platform
        should("test askId") {
            // uncomment below to test the property
            //modelInstance.askId shouldBe ("TODO")
        }

        // to test the property `orderNumber` - The unique order number. Standard example: 323314425-323214184. Flex example: 02-L0QT6MRVSG
        should("test orderNumber") {
            // uncomment below to test the property
            //modelInstance.orderNumber shouldBe ("TODO")
        }

        // to test the property `listingId` - Unique ID for this listing
        should("test listingId") {
            // uncomment below to test the property
            //modelInstance.listingId shouldBe ("TODO")
        }

        // to test the property `amount` - The ask/order price
        should("test amount") {
            // uncomment below to test the property
            //modelInstance.amount shouldBe ("TODO")
        }

        // to test the property `currencyCode` - The currency type for this order.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
        should("test currencyCode") {
            // uncomment below to test the property
            //modelInstance.currencyCode shouldBe ("TODO")
        }

        // to test the property `createdAt` - When the order was created in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z
        should("test createdAt") {
            // uncomment below to test the property
            //modelInstance.createdAt shouldBe ("TODO")
        }

        // to test the property `updatedAt` - When the order was updated in UTC. Represented as ISO 8601 format like 2021-08-25T13:51:47.000Z
        should("test updatedAt") {
            // uncomment below to test the property
            //modelInstance.updatedAt shouldBe ("TODO")
        }

        // to test the property `variant`
        should("test variant") {
            // uncomment below to test the property
            //modelInstance.variant shouldBe ("TODO")
        }

        // to test the property `product`
        should("test product") {
            // uncomment below to test the property
            //modelInstance.product shouldBe ("TODO")
        }

        // to test the property `status`
        should("test status") {
            // uncomment below to test the property
            //modelInstance.status shouldBe ("TODO")
        }

        // to test the property `shipment` - The shipment details of the order.
        should("test shipment") {
            // uncomment below to test the property
            //modelInstance.shipment shouldBe ("TODO")
        }

        // to test the property `initiatedShipments` - An object containing details about the seller initiated shipments.
        should("test initiatedShipments") {
            // uncomment below to test the property
            //modelInstance.initiatedShipments shouldBe ("TODO")
        }

        // to test the property `inventoryType`
        should("test inventoryType") {
            // uncomment below to test the property
            //modelInstance.inventoryType shouldBe ("TODO")
        }

        // to test the property `payout`
        should("test payout") {
            // uncomment below to test the property
            //modelInstance.payout shouldBe ("TODO")
        }

        // to test the property `authenticationDetails` - Details about authentication status and failure notes
        should("test authenticationDetails") {
            // uncomment below to test the property
            //modelInstance.authenticationDetails shouldBe ("TODO")
        }

    }
}
