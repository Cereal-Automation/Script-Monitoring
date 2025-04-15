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

import com.cereal.stockx.api.model.VariantMarketData

class VariantMarketDataTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of VariantMarketData
        //val modelInstance = VariantMarketData()

        // to test the property `productId` - Unique identifier for this product
        should("test productId") {
            // uncomment below to test the property
            //modelInstance.productId shouldBe ("TODO")
        }

        // to test the property `variantId` - Unique identifier for this product variant
        should("test variantId") {
            // uncomment below to test the property
            //modelInstance.variantId shouldBe ("TODO")
        }

        // to test the property `currencyCode` - The currency code. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API.<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
        should("test currencyCode") {
            // uncomment below to test the property
            //modelInstance.currencyCode shouldBe ("TODO")
        }

        // to test the property `highestBidAmount` - The highest bid for the product variant listed in the country requested.
        should("test highestBidAmount") {
            // uncomment below to test the property
            //modelInstance.highestBidAmount shouldBe ("TODO")
        }

        // to test the property `lowestAskAmount` - The lowest ask for the product variant listed in the country requested.
        should("test lowestAskAmount") {
            // uncomment below to test the property
            //modelInstance.lowestAskAmount shouldBe ("TODO")
        }

        // to test the property `sellFasterAmount` - The price you have to list at, inclusive of duties and taxes, to become the lowest Ask to buyers in the United States.
        should("test sellFasterAmount") {
            // uncomment below to test the property
            //modelInstance.sellFasterAmount shouldBe ("TODO")
        }

        // to test the property `earnMoreAmount` - The price you have to list at, to become the lowest ask to buyers in your region. This accounts for VAT and taxes.
        should("test earnMoreAmount") {
            // uncomment below to test the property
            //modelInstance.earnMoreAmount shouldBe ("TODO")
        }

        // to test the property `flexLowestAskAmount` - The Flex program's lowest ask for the product variant listed in the country requested.
        should("test flexLowestAskAmount") {
            // uncomment below to test the property
            //modelInstance.flexLowestAskAmount shouldBe ("TODO")
        }

    }
}
