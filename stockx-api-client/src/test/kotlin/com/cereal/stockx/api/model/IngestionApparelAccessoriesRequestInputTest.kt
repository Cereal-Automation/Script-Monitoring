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

import com.cereal.stockx.api.model.IngestionApparelAccessoriesRequestInput
import com.cereal.stockx.api.model.PRODUCTCATEGORIES
import com.cereal.stockx.api.model.ProductCode

class IngestionApparelAccessoriesRequestInputTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of IngestionApparelAccessoriesRequestInput
        //val modelInstance = IngestionApparelAccessoriesRequestInput()

        // to test the property `category`
        should("test category") {
            // uncomment below to test the property
            //modelInstance.category shouldBe ("TODO")
        }

        // to test the property `productTitle` - A string that uniquely identifies the name of the product.
        should("test productTitle") {
            // uncomment below to test the property
            //modelInstance.productTitle shouldBe ("TODO")
        }

        // to test the property `brand` - The brand of the product.
        should("test brand") {
            // uncomment below to test the property
            //modelInstance.brand shouldBe ("TODO")
        }

        // to test the property `productImages` - Public facing links to the products image.
        should("test productImages") {
            // uncomment below to test the property
            //modelInstance.productImages shouldBe ("TODO")
        }

        // to test the property `variants` - The product variants available to sell. For example, for sneakers, each different size is a different variant or for electronics like iPhone, each different storage capacity is a different variant.
        should("test variants") {
            // uncomment below to test the property
            //modelInstance.variants shouldBe ("TODO")
        }

        // to test the property `retailPrice` - The products retail price.
        should("test retailPrice") {
            // uncomment below to test the property
            //modelInstance.retailPrice shouldBe ("TODO")
        }

        // to test the property `releaseDate` - The products release date.
        should("test releaseDate") {
            // uncomment below to test the property
            //modelInstance.releaseDate shouldBe ("TODO")
        }

        // to test the property `countryOfOrigin` - The country in which the product was manufactured in ISO Alpha 2 Format.
        should("test countryOfOrigin") {
            // uncomment below to test the property
            //modelInstance.countryOfOrigin shouldBe ("TODO")
        }

        // to test the property `colorway` - The combinations of colors in which the product is designed.
        should("test colorway") {
            // uncomment below to test the property
            //modelInstance.colorway shouldBe ("TODO")
        }

        // to test the property `material` - Brand listed materials.
        should("test material") {
            // uncomment below to test the property
            //modelInstance.material shouldBe ("TODO")
        }

        // to test the property `gender` - The products targeted gender.
        should("test gender") {
            // uncomment below to test the property
            //modelInstance.gender shouldBe ("TODO")
        }

        // to test the property `partnerProductId` - An external ID that partners will use to reference an internal StockX catalog item if approved. Note that this is the higher level product id, not the variantId.
        should("test partnerProductId") {
            // uncomment below to test the property
            //modelInstance.partnerProductId shouldBe ("TODO")
        }

        // to test the property `tags` - A list of attributes or short descriptors associated with the product.
        should("test tags") {
            // uncomment below to test the property
            //modelInstance.tags shouldBe ("TODO")
        }

        // to test the property `styleCode` - The Style Code for the product  @example \"M990BK5\"
        should("test styleCode") {
            // uncomment below to test the property
            //modelInstance.styleCode shouldBe ("TODO")
        }

        // to test the property `gtin` - The products global trade item number.
        should("test gtin") {
            // uncomment below to test the property
            //modelInstance.gtin shouldBe ("TODO")
        }

        // to test the property `productDescription` - Open text field used for describing the product to the customer.
        should("test productDescription") {
            // uncomment below to test the property
            //modelInstance.productDescription shouldBe ("TODO")
        }

        // to test the property `dimensions` - A string of the dimensions of the product.
        should("test dimensions") {
            // uncomment below to test the property
            //modelInstance.dimensions shouldBe ("TODO")
        }

        // to test the property `weight` - The weight of the product.
        should("test weight") {
            // uncomment below to test the property
            //modelInstance.weight shouldBe ("TODO")
        }

        // to test the property `productURL` - Official third party product URL.
        should("test productURL") {
            // uncomment below to test the property
            //modelInstance.productURL shouldBe ("TODO")
        }

        // to test the property `productCode`
        should("test productCode") {
            // uncomment below to test the property
            //modelInstance.productCode shouldBe ("TODO")
        }

    }
}
