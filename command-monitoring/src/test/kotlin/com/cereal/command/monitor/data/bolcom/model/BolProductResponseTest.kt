package com.cereal.command.monitor.data.bolcom.model

import kotlinx.serialization.json.JsonNull
import kotlin.test.Test
import kotlin.test.assertEquals

class BolProductResponseTest {
    @Test
    fun `parseBolProducts skips a malformed product without losing the rest of the batch`() {
        val json =
            """
            {
              "products": [
                { "id": "1", "title": "Valid product 1", "url": "/one" },
                { "id": "2", "url": "/missing-title" },
                { "id": "3", "title": "Valid product 2", "url": "/two" }
              ]
            }
            """.trimIndent()

        val products = parseBolProducts(json)

        assertEquals(listOf("1", "3"), products.map { it.id })
    }

    @Test
    fun `parseBolProducts returns an empty list instead of throwing for an unexpected element shape`() {
        assertEquals(emptyList(), parseBolProducts(JsonNull))
    }

    @Test
    fun `parseBolProducts treats a sentinel value in place of retailActions as an empty list`() {
        val json =
            """
            {
              "products": [
                {
                  "id": "1",
                  "title": "Test product",
                  "url": "/test",
                  "bestSellingOffer": {
                    "retailActions": -5
                  }
                }
              ]
            }
            """.trimIndent()

        val products = parseBolProducts(json)

        assertEquals(1, products.size)
        assertEquals(emptyList(), products.first().bestSellingOffer?.retailActions)
    }

    @Test
    fun `parseBolProducts decodes Money amount as a string in sellingPrice but a number in savings`() {
        val json =
            """
            {
              "products": [
                {
                  "id": "1",
                  "title": "Test product",
                  "url": "/test",
                  "bestSellingOffer": {
                    "sellingPrice": { "price": { "amount": "30.42" } },
                    "savings": {
                      "reference": {
                        "referencePrice": { "amount": 34.99 },
                        "sellingPriceDiscount": { "percentage": 13, "amount": { "amount": 4.57 } }
                      }
                    }
                  }
                }
              ]
            }
            """.trimIndent()

        val product = parseBolProducts(json).first()

        assertEquals("30.42", product.price)
        assertEquals("34.99", product.referencePrice)
        assertEquals(13, product.discountPercentage)
        assertEquals("4.57", product.bestSellingOffer?.discountAmount)
    }
}
