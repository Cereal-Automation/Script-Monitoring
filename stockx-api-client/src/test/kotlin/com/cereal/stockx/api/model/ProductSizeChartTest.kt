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

import com.cereal.stockx.api.model.ProductSizeChart
import com.cereal.stockx.api.model.ProductSizeConversion

class ProductSizeChartTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of ProductSizeChart
        //val modelInstance = ProductSizeChart()

        // to test the property `availableConversions` - The available size chart conversions this product has
        should("test availableConversions") {
            // uncomment below to test the property
            //modelInstance.availableConversions shouldBe ("TODO")
        }

        // to test the property `defaultConversion` - The default sizing this product uses
        should("test defaultConversion") {
            // uncomment below to test the property
            //modelInstance.defaultConversion shouldBe ("TODO")
        }

    }
}
