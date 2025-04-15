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

import com.cereal.stockx.api.model.PRODUCTCATEGORIES
import com.cereal.stockx.api.model.ProductCode

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param category 
 * @param productTitle A string that uniquely identifies the name of the product.
 * @param brand The brand of the product.
 * @param productImages Public facing links to the products image.
 * @param variants The product variants available to sell. For example, for sneakers, each different size is a different variant or for electronics like iPhone, each different storage capacity is a different variant.
 * @param retailPrice The products retail price.
 * @param releaseDate The products release date.
 * @param countryOfOrigin The country in which the product was manufactured in ISO Alpha 2 Format.
 * @param partnerProductId An external ID that partners will use to reference an internal StockX catalog item if approved. Note that this is the higher level product id, not the variantId.
 * @param tags A list of attributes or short descriptors associated with the product.
 * @param colorway The combinations of colors in which the product is designed.
 * @param styleCode The Style Code for the product  @example \"M990BK5\"
 * @param productDescription Open text field used for describing the product to the customer.
 * @param dimensions A string of the dimensions of the product.
 * @param weight The weight of the product.
 * @param productURL Official third party product URL.
 * @param productCode 
 */


data class IngestionCollectiblesRequestInput (

    @Json(name = "category")
    val category: PRODUCTCATEGORIES,

    /* A string that uniquely identifies the name of the product. */
    @Json(name = "productTitle")
    val productTitle: kotlin.String,

    /* The brand of the product. */
    @Json(name = "brand")
    val brand: kotlin.String,

    /* Public facing links to the products image. */
    @Json(name = "productImages")
    val productImages: kotlin.collections.List<kotlin.String>,

    /* The product variants available to sell. For example, for sneakers, each different size is a different variant or for electronics like iPhone, each different storage capacity is a different variant. */
    @Json(name = "variants")
    val variants: kotlin.collections.List<kotlin.String>,

    /* The products retail price. */
    @Json(name = "retailPrice")
    val retailPrice: kotlin.String,

    /* The products release date. */
    @Json(name = "releaseDate")
    val releaseDate: kotlin.String,

    /* The country in which the product was manufactured in ISO Alpha 2 Format. */
    @Json(name = "countryOfOrigin")
    val countryOfOrigin: kotlin.String,

    /* An external ID that partners will use to reference an internal StockX catalog item if approved. Note that this is the higher level product id, not the variantId. */
    @Json(name = "partnerProductId")
    val partnerProductId: kotlin.String? = null,

    /* A list of attributes or short descriptors associated with the product. */
    @Json(name = "tags")
    val tags: kotlin.collections.List<kotlin.String>? = null,

    /* The combinations of colors in which the product is designed. */
    @Json(name = "colorway")
    val colorway: kotlin.String? = null,

    /* The Style Code for the product  @example \"M990BK5\" */
    @Json(name = "styleCode")
    val styleCode: kotlin.String? = null,

    /* Open text field used for describing the product to the customer. */
    @Json(name = "productDescription")
    val productDescription: kotlin.String? = null,

    /* A string of the dimensions of the product. */
    @Json(name = "dimensions")
    val dimensions: kotlin.String? = null,

    /* The weight of the product. */
    @Json(name = "weight")
    val weight: kotlin.String? = null,

    /* Official third party product URL. */
    @Json(name = "productURL")
    val productURL: kotlin.String? = null,

    @Json(name = "productCode")
    val productCode: ProductCode? = null

) {


}

