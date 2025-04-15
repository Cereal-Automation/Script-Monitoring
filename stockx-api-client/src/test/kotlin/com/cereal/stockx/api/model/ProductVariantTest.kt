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

import com.cereal.stockx.api.model.ProductVariant
import com.cereal.stockx.api.model.ProductVariantDetailsSizeChart

class ProductVariantTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of ProductVariant
        //val modelInstance = ProductVariant()

        // to test the property `productId` - Unique identifier for a product
        should("test productId") {
            // uncomment below to test the property
            //modelInstance.productId shouldBe ("TODO")
        }

        // to test the property `variantId` - Unique identifier for a products variant
        should("test variantId") {
            // uncomment below to test the property
            //modelInstance.variantId shouldBe ("TODO")
        }

        // to test the property `variantName` - SKU of the variant
        should("test variantName") {
            // uncomment below to test the property
            //modelInstance.variantName shouldBe ("TODO")
        }

        // to test the property `variantValue` - Variant's value
        should("test variantValue") {
            // uncomment below to test the property
            //modelInstance.variantValue shouldBe ("TODO")
        }

        // to test the property `sizeChart`
        should("test sizeChart") {
            // uncomment below to test the property
            //modelInstance.sizeChart shouldBe ("TODO")
        }

    }
}
