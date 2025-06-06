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

import com.cereal.stockx.api.model.Shipment

class ShipmentTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of Shipment
        //val modelInstance = Shipment()

        // to test the property `shipByDate` - Date that the order should be shipped by.
        should("test shipByDate") {
            // uncomment below to test the property
            //modelInstance.shipByDate shouldBe ("TODO")
        }

        // to test the property `trackingNumber` - The shipments tracking number
        should("test trackingNumber") {
            // uncomment below to test the property
            //modelInstance.trackingNumber shouldBe ("TODO")
        }

        // to test the property `trackingUrl` - The URL to the carriers web page.
        should("test trackingUrl") {
            // uncomment below to test the property
            //modelInstance.trackingUrl shouldBe ("TODO")
        }

        // to test the property `carrierCode` - The carrier code for the shipment provider.
        should("test carrierCode") {
            // uncomment below to test the property
            //modelInstance.carrierCode shouldBe ("TODO")
        }

        // to test the property `shippingLabelUrl` - The URL of the shipping label
        should("test shippingLabelUrl") {
            // uncomment below to test the property
            //modelInstance.shippingLabelUrl shouldBe ("TODO")
        }

        // to test the property `shippingDocumentUrl` - The URL of the StockX shipping document.
        should("test shippingDocumentUrl") {
            // uncomment below to test the property
            //modelInstance.shippingDocumentUrl shouldBe ("TODO")
        }

    }
}
