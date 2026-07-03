package com.bol.mapping

import kotlin.test.Test
import kotlin.test.assertEquals

class BolProductResponseTest {
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
