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

import com.cereal.stockx.api.model.BatchCreateListingResponse

class BatchCreateListingResponseTest : ShouldSpec() {
    init {
        // uncomment below to create an instance of BatchCreateListingResponse
        //val modelInstance = BatchCreateListingResponse()

        // to test the property `batchId` - Unique Batch ID
        should("test batchId") {
            // uncomment below to test the property
            //modelInstance.batchId shouldBe ("TODO")
        }

        // to test the property `status` - The status of the batch
        should("test status") {
            // uncomment below to test the property
            //modelInstance.status shouldBe ("TODO")
        }

        // to test the property `completedAt` - When the batch fully completed
        should("test completedAt") {
            // uncomment below to test the property
            //modelInstance.completedAt shouldBe ("TODO")
        }

        // to test the property `createdAt` - when this batch was created in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z
        should("test createdAt") {
            // uncomment below to test the property
            //modelInstance.createdAt shouldBe ("TODO")
        }

        // to test the property `updatedAt` - When this batch was last updated in UTC. Represented as ISO 8601 format like 2021-11-09T12:44:31.000Z
        should("test updatedAt") {
            // uncomment below to test the property
            //modelInstance.updatedAt shouldBe ("TODO")
        }

        // to test the property `totalItems` - Total number of items in this batch
        should("test totalItems") {
            // uncomment below to test the property
            //modelInstance.totalItems shouldBe ("TODO")
        }

        // to test the property `itemStatuses` - The number of items in this batch grouped by their statuses. This is a short-hand way to quickly introspect how many items are still enqued, how many succeeded or how many failed in a batch.
        should("test itemStatuses") {
            // uncomment below to test the property
            //modelInstance.itemStatuses shouldBe ("TODO")
        }

    }
}
