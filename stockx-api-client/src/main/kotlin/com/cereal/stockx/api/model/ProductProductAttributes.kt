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


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Object containing the attributes of the product.
 *
 * @param gender Gender targeted product
 * @param season The season the product was released
 * @param releaseDate The date the product was released
 * @param retailPrice The retail price of the product.
 * @param colorway The combinations of colors in which the product is designed
 * @param color Color of product
 */


data class ProductProductAttributes (

    /* Gender targeted product */
    @Json(name = "gender")
    val gender: kotlin.String?,

    /* The season the product was released */
    @Json(name = "season")
    val season: kotlin.String?,

    /* The date the product was released */
    @Json(name = "releaseDate")
    val releaseDate: kotlin.String?,

    /* The retail price of the product. */
    @Json(name = "retailPrice")
    val retailPrice: kotlin.Double?,

    /* The combinations of colors in which the product is designed */
    @Json(name = "colorway")
    val colorway: kotlin.String?,

    /* Color of product */
    @Json(name = "color")
    val color: kotlin.String?

) {


}

