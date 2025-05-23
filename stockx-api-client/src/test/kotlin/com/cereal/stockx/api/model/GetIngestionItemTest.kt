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

import com.cereal.stockx.api.model.GetIngestionItem
import com.cereal.stockx.api.model.GetIngestionItemInput
import com.cereal.stockx.api.model.INGESTIONITEMSTATUS
import com.cereal.stockx.api.model.IngestionItemRejectionDetails
import com.cereal.stockx.api.model.IngestionItemResult

class GetIngestionItemTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of GetIngestionItem
        //val modelInstance = GetIngestionItem()

        // to test the property `ingestionId` - Unique ID used to return the status of an ingestion job.
        should("test ingestionId") {
            // uncomment below to test the property
            //modelInstance.ingestionId shouldBe ("TODO")
        }

        // to test the property `status`
        should("test status") {
            // uncomment below to test the property
            //modelInstance.status shouldBe ("TODO")
        }

        // to test the property `input`
        should("test input") {
            // uncomment below to test the property
            //modelInstance.input shouldBe ("TODO")
        }

        // to test the property `result` - The result of the ingestion job if the status is completed.
        should("test result") {
            // uncomment below to test the property
            //modelInstance.result shouldBe ("TODO")
        }

        // to test the property `partnerProductId` - The external partner ID provided in the request.
        should("test partnerProductId") {
            // uncomment below to test the property
            //modelInstance.partnerProductId shouldBe ("TODO")
        }

        // to test the property `rejectionDetails` - The details about its rejection if REJECTED
        should("test rejectionDetails") {
            // uncomment below to test the property
            //modelInstance.rejectionDetails shouldBe ("TODO")
        }

        // to test the property `createdAt` - Timestamp when the resource was created.
        should("test createdAt") {
            // uncomment below to test the property
            //modelInstance.createdAt shouldBe ("TODO")
        }

        // to test the property `updatedAt` - Timestamp when the resource was last updated.
        should("test updatedAt") {
            // uncomment below to test the property
            //modelInstance.updatedAt shouldBe ("TODO")
        }

    }
}
