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
 * 
 *
 * Values: CREATE,UPDATE,DELETE,ACTIVATE,DEACTIVATE,CANCEL_ORDER,RECLAIM,CANCEL_RECLAIM
 */

@JsonClass(generateAdapter = false)
enum class OperationType(val value: kotlin.String) {

    @Json(name = "CREATE")
    CREATE("CREATE"),

    @Json(name = "UPDATE")
    UPDATE("UPDATE"),

    @Json(name = "DELETE")
    DELETE("DELETE"),

    @Json(name = "ACTIVATE")
    ACTIVATE("ACTIVATE"),

    @Json(name = "DEACTIVATE")
    DEACTIVATE("DEACTIVATE"),

    @Json(name = "CANCEL_ORDER")
    CANCEL_ORDER("CANCEL_ORDER"),

    @Json(name = "RECLAIM")
    RECLAIM("RECLAIM"),

    @Json(name = "CANCEL_RECLAIM")
    CANCEL_RECLAIM("CANCEL_RECLAIM");

    /**
     * Override [toString()] to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): kotlin.String = value

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: kotlin.Any?): kotlin.String? = if (data is OperationType) "$data" else null

        /**
         * Returns a valid [OperationType] for [data], null otherwise.
         */
        fun decode(data: kotlin.Any?): OperationType? = data?.let {
          val normalizedData = "$it".lowercase()
          values().firstOrNull { value ->
            it == value || normalizedData == "$value".lowercase()
          }
        }
    }
}

