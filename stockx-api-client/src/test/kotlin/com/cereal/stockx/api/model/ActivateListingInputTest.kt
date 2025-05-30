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

import com.cereal.stockx.api.model.ActivateListingInput

class ActivateListingInputTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of ActivateListingInput
        //val modelInstance = ActivateListingInput()

        // to test the property `amount` - The amount this product is being listed for
        should("test amount") {
            // uncomment below to test the property
            //modelInstance.amount shouldBe ("TODO")
        }

        // to test the property `currencyCode` - The currency code this product is being listed in. If not provided, it will default to USD.  Only valid currencies supported on stockx.com are supported via API<br><br>Available values: \"AUD\", \"CAD\", \"CHF\", \"EUR\", \"GBP\", \"HKD\", \"JPY\", \"KRW\", \"MXN\", \"NZD\", \"SGD\", \"USD\"
        should("test currencyCode") {
            // uncomment below to test the property
            //modelInstance.currencyCode shouldBe ("TODO")
        }

        // to test the property `expiresAt` - UTC timestamp representing when this Ask should auto-expire.  If not provided, it will default to 365 days from today.  Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z
        should("test expiresAt") {
            // uncomment below to test the property
            //modelInstance.expiresAt shouldBe ("TODO")
        }

    }
}
