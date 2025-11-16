package com.cereal.command.monitor.data.bolcom

import com.cereal.command.monitor.models.ItemProperty
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.models.proxy.RandomProxy
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BolcomWebDataSourceTest {
    private val mockLogRepository = mockk<LogRepository>(relaxed = true)
    private val mockRandomProxy = mockk<RandomProxy>(relaxed = true)

    @Test
    fun `should create BolcomWebDataSource successfully`() {
        val dataSource = BolcomWebDataSource(mockLogRepository, mockRandomProxy)
        assertNotNull(dataSource)
    }

    @Test
    fun `should parse product from search result with full data`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "1234567890",
                                          "title": "Test Product",
                                          "url": "/nl/nl/p/test-product/1234567890/",
                                          "relatedParties": [
                                            {
                                              "role": "BRAND",
                                              "party": {
                                                "name": "Test Brand"
                                              }
                                            }
                                          ],
                                          "assets": [
                                            {
                                              "renditions": [
                                                {
                                                  "url": "https://example.com/image.jpg"
                                                }
                                              ]
                                            }
                                          ],
                                          "attributes": [
                                            {
                                              "name": "Description",
                                              "values": [
                                                {
                                                  "value": "Test Description"
                                                }
                                              ]
                                            }
                                          ],
                                          "bestSellingOffer": {
                                            "sellingPrice": {
                                              "price": {
                                                "amount": 19.99
                                              }
                                            },
                                            "sellingPriceDiscountOnStrikethroughPrice": {
                                              "amount": {
                                                "amount": 5.00
                                              }
                                            },
                                            "strikethroughPrice": {
                                              "price": {
                                                "amount": 24.99
                                              }
                                            },
                                            "retailer": {
                                              "name": "Test Seller"
                                            },
                                            "bestDeliveryOption": {
                                              "deliveryDescription": "Op voorraad"
                                            }
                                          }
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            // Use reflection to inject the mock client
            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("1234567890")

            assertEquals(1, items.size)
            val item = items.first()
            assertEquals("1234567890", item.id)
            assertEquals("Test Product", item.name)
            assertEquals("https://www.bol.com/nl/nl/p/test-product/1234567890/", item.url)
            assertEquals("Test Description", item.description)
            assertEquals("https://example.com/image.jpg", item.imageUrl)

            // Verify properties
            val stockProperty = item.properties.filterIsInstance<ItemProperty.Stock>().first()
            assertTrue(stockProperty.isInStock)
            assertEquals(":white_check_mark:", stockProperty.level)

            val sellerProperty = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "Seller" }
            assertNotNull(sellerProperty)
            assertEquals("Test Seller", sellerProperty.value)

            val brandProperty = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "Brand" }
            assertNotNull(brandProperty)
            assertEquals("Test Brand", brandProperty.value)

            val priceProperty = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "Price" }
            assertNotNull(priceProperty)
            assertTrue(priceProperty.value.contains("19") || priceProperty.value.contains("20"))

            val discountProperty = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "Discount" }
            assertNotNull(discountProperty)

            mockClient.close()
        }

    @Test
    fun `should parse product with minimal data`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "9876543210",
                                          "title": "Minimal Product",
                                          "url": "/nl/nl/p/minimal/9876543210/",
                                          "bestSellingOffer": {
                                            "sellingPrice": {
                                              "price": {
                                                "amount": 9.99
                                              }
                                            },
                                            "deliveredWithin48Hours": true
                                          }
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("9876543210")

            assertEquals(1, items.size)
            val item = items.first()
            assertEquals("9876543210", item.id)
            assertEquals("Minimal Product", item.name)

            // Verify stock property shows orderable
            val stockProperty = item.properties.filterIsInstance<ItemProperty.Stock>().first()
            assertTrue(stockProperty.isInStock)

            mockClient.close()
        }

    @Test
    fun `should return empty list when no products found`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": []
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("0000000000")

            assertEquals(0, items.size)

            mockClient.close()
        }

    @Test
    fun `should handle out of stock products`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "5555555555",
                                          "title": "Out of Stock Product",
                                          "url": "/nl/nl/p/out-of-stock/5555555555/",
                                          "bestSellingOffer": {
                                            "sellingPrice": {
                                              "price": {
                                                "amount": 29.99
                                              }
                                            },
                                            "deliveredWithin48Hours": false,
                                            "bestDeliveryOption": {
                                              "deliveryDescription": "Temporarily unavailable"
                                            }
                                          }
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("5555555555")

            assertEquals(1, items.size)
            val stockProperty = items.first().properties.filterIsInstance<ItemProperty.Stock>().first()
            assertEquals(false, stockProperty.isInStock)
            assertEquals(":x:", stockProperty.level)

            mockClient.close()
        }

    @Test
    fun `should filter out products without title`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "1111111111",
                                          "url": "/nl/nl/p/test/1111111111/"
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("1111111111")

            // Should filter out product without title
            assertEquals(0, items.size)

            mockClient.close()
        }

    @Test
    fun `should handle products with regular price and discount`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "7777777777",
                                          "title": "Discounted Product",
                                          "url": "/nl/nl/p/discounted/7777777777/",
                                          "bestSellingOffer": {
                                            "sellingPrice": {
                                              "price": {
                                                "amount": 79.99
                                              }
                                            },
                                            "sellingPriceDiscountOnStrikethroughPrice": {
                                              "amount": {
                                                "amount": 20.00
                                              }
                                            },
                                            "strikethroughPrice": {
                                              "price": {
                                                "amount": 99.99
                                              }
                                            },
                                            "deliveredWithin48Hours": true
                                          }
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("7777777777")

            assertEquals(1, items.size)
            val item = items.first()

            // Verify discount property exists
            val discountProperty = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "Discount" }
            assertNotNull(discountProperty)

            // Verify price property includes strikethrough
            val priceProperty = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "Price" }
            assertNotNull(priceProperty)
            assertTrue(priceProperty.value.contains("~~"))

            mockClient.close()
        }

    @Test
    fun `should handle products with quick delivery`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "8888888888",
                                          "title": "Quick Delivery Product",
                                          "url": "/nl/nl/p/quick/8888888888/",
                                          "bestSellingOffer": {
                                            "sellingPrice": {
                                              "price": {
                                                "amount": 39.99
                                              }
                                            },
                                            "bestDeliveryOption": {
                                              "deliveryDescription": "Voor 23:00 besteld, morgen in huis"
                                            }
                                          }
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("8888888888")

            assertEquals(1, items.size)
            val stockProperty = items.first().properties.filterIsInstance<ItemProperty.Stock>().first()
            assertTrue(stockProperty.isInStock)
            assertEquals(":white_check_mark:", stockProperty.level)

            mockClient.close()
        }

    @Test
    fun `should skip non-ProductItem slot types`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "BannerItem",
                                      "props": {}
                                    },
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "9999999999",
                                          "title": "Valid Product",
                                          "url": "/nl/nl/p/valid/9999999999/"
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("9999999999")

            // Should only get 1 product, not the BannerItem
            assertEquals(1, items.size)
            assertEquals("9999999999", items.first().id)

            mockClient.close()
        }

    @Test
    fun `should handle multiple products in search results`() =
        runTest {
            val jsonResponse =
                """
                {
                  "customer": {
                    "basket": {
                      "totalQuantity": {
                        "routes/searchPage": {
                          "data": {
                            "template": {
                              "regions": [
                                {
                                  "slots": [
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "1111111111",
                                          "title": "Product 1",
                                          "url": "/nl/nl/p/product1/1111111111/"
                                        }
                                      }
                                    },
                                    {
                                      "type": "ProductItem",
                                      "props": {
                                        "product": {
                                          "id": "2222222222",
                                          "title": "Product 2",
                                          "url": "/nl/nl/p/product2/2222222222/"
                                        }
                                      }
                                    }
                                  ]
                                }
                              ]
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()

            val mockEngine =
                MockEngine { request ->
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val mockClient =
                HttpClient(mockEngine) {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            },
                        )
                    }
                }

            val dataSource = BolcomWebDataSource(mockLogRepository, null)
            val clientField = dataSource.javaClass.getDeclaredField("client")
            clientField.isAccessible = true
            clientField.set(dataSource, mockClient)

            val items = dataSource.fetchItemsByEan("test")

            assertEquals(2, items.size)
            assertEquals("1111111111", items[0].id)
            assertEquals("Product 1", items[0].name)
            assertEquals("2222222222", items[1].id)
            assertEquals("Product 2", items[1].name)

            mockClient.close()
        }
}
