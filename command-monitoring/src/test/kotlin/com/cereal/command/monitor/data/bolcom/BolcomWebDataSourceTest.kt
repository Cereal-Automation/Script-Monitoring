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
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BolcomWebDataSourceTest {
    private val mockLogRepository = mockk<LogRepository>(relaxed = true)
    private val mockRandomProxy = mockk<RandomProxy>(relaxed = true)

    private suspend fun dataSourceRespondingWith(jsonResponse: String): BolcomWebDataSource {
        val mockEngine =
            MockEngine { _ ->
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

        val dataSource = BolcomWebDataSource(mockLogRepository, mockRandomProxy)
        val clientField = dataSource.javaClass.getDeclaredField("client")
        clientField.isAccessible = true
        clientField.set(dataSource, mockClient)
        return dataSource
    }

    @Test
    fun `should create BolcomWebDataSource successfully`() {
        val dataSource = BolcomWebDataSource(mockLogRepository, mockRandomProxy)
        assertNotNull(dataSource)
    }

    @Test
    fun `should parse product from search result with full data`() =
        runTest {
            val dataSource =
                dataSourceRespondingWith(
                    """
                    {
                      "products": [
                        {
                          "id": "1234567890",
                          "title": "Test Product",
                          "url": "/nl/nl/p/test-product/1234567890/",
                          "relatedParties": [
                            { "role": "BRAND", "party": { "name": "Test Brand" } }
                          ],
                          "assets": [
                            { "renditions": [ { "url": "https://example.com/image.jpg" } ] }
                          ],
                          "attributes": [
                            { "name": "Description", "values": [ { "value": "Test Description" } ] }
                          ],
                          "bestSellingOffer": {
                            "sellingPrice": { "price": { "amount": "19.99" } },
                            "retailer": { "name": "Test Seller" },
                            "bestDeliveryOption": { "deliveryDescription": "Op voorraad. Voor 23:59 uur besteld, morgen in huis" },
                            "promotionalLabels": [ { "titleText": "deal" } ]
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                )

            val items = dataSource.fetchItemsByEan("1234567890")

            assertEquals(1, items.size)
            val item = items.first()
            assertEquals("1234567890", item.id)
            assertEquals("Test Product", item.name)
            assertEquals("https://www.bol.com/nl/nl/p/test-product/1234567890/", item.url)
            assertEquals("Test Description", item.description)
            assertEquals("https://example.com/image.jpg", item.imageUrl)

            val price = item.properties.filterIsInstance<ItemProperty.Price>().first()
            assertEquals(BigDecimal("19.99"), price.value)

            val stock = item.properties.filterIsInstance<ItemProperty.Stock>().first()
            assertTrue(stock.isInStock)
            assertNull(stock.level)

            val custom = item.properties.filterIsInstance<ItemProperty.Custom>()
            assertEquals("Test Seller", custom.first { it.name == "Seller" }.value)
            assertEquals("Test Brand", custom.first { it.name == "Brand" }.value)
            assertEquals(
                "Op voorraad. Voor 23:59 uur besteld, morgen in huis",
                custom.first { it.name == "Shipping" }.value,
            )

            // A promotional/campaign label ("deal") is not the product's own discount.
            assertNull(custom.find { it.name == "Discount" })
        }

    @Test
    fun `should parse product with minimal data`() =
        runTest {
            val dataSource =
                dataSourceRespondingWith(
                    """
                    {
                      "products": [
                        {
                          "id": "9876543210",
                          "title": "Minimal Product",
                          "url": "/nl/nl/p/minimal/9876543210/",
                          "bestSellingOffer": {
                            "sellingPrice": { "price": { "amount": "9.99" } },
                            "deliveredWithin48Hours": true
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                )

            val items = dataSource.fetchItemsByEan("9876543210")

            assertEquals(1, items.size)
            val item = items.first()
            assertEquals("9876543210", item.id)
            assertEquals("Minimal Product", item.name)
            assertNull(item.description)
            assertNull(item.imageUrl)

            val stock = item.properties.filterIsInstance<ItemProperty.Stock>().first()
            assertTrue(stock.isInStock)
        }

    @Test
    fun `should return empty list when no products found`() =
        runTest {
            val dataSource = dataSourceRespondingWith("""{ "products": [] }""")

            val items = dataSource.fetchItemsByEan("0000000000")

            assertEquals(0, items.size)
        }

    @Test
    fun `should return empty list instead of throwing when the response body is not valid JSON`() =
        runTest {
            val dataSource = dataSourceRespondingWith("<html><body>Service Unavailable</body></html>")

            val items = dataSource.fetchItemsByEan("0000000000")

            assertEquals(0, items.size)
        }

    @Test
    fun `should mark product as unavailable when there is no selling offer`() =
        runTest {
            val dataSource =
                dataSourceRespondingWith(
                    """
                    {
                      "products": [
                        {
                          "id": "5555555555",
                          "title": "Unavailable Product",
                          "url": "/nl/nl/p/unavailable/5555555555/"
                        }
                      ]
                    }
                    """.trimIndent(),
                )

            val items = dataSource.fetchItemsByEan("5555555555")

            assertEquals(1, items.size)
            val stock = items.first().properties.filterIsInstance<ItemProperty.Stock>().first()
            assertEquals(false, stock.isInStock)
            assertEquals("Unavailable", stock.level)
        }

    @Test
    fun `should mark product as preorder when a release date is set`() =
        runTest {
            val dataSource =
                dataSourceRespondingWith(
                    """
                    {
                      "products": [
                        {
                          "id": "6666666666",
                          "title": "Preorder Product",
                          "url": "/nl/nl/p/preorder/6666666666/",
                          "bestSellingOffer": {
                            "sellingPrice": { "price": { "amount": "59.99" } },
                            "bestDeliveryOption": { "productReleaseDate": "2026-08-01" }
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                )

            val items = dataSource.fetchItemsByEan("6666666666")

            assertEquals(1, items.size)
            val stock = items.first().properties.filterIsInstance<ItemProperty.Stock>().first()
            assertEquals(false, stock.isInStock)
            assertEquals("Preorder", stock.level)
        }

    @Test
    fun `should mark product as low stock when scarce`() =
        runTest {
            val dataSource =
                dataSourceRespondingWith(
                    """
                    {
                      "products": [
                        {
                          "id": "4444444444",
                          "title": "Scarce Product",
                          "url": "/nl/nl/p/scarce/4444444444/",
                          "bestSellingOffer": {
                            "sellingPrice": { "price": { "amount": "15.00" } },
                            "isScarce": true
                          }
                        }
                      ]
                    }
                    """.trimIndent(),
                )

            val items = dataSource.fetchItemsByEan("4444444444")

            assertEquals(1, items.size)
            val stock = items.first().properties.filterIsInstance<ItemProperty.Stock>().first()
            assertTrue(stock.isInStock)
            assertEquals("Low stock", stock.level)
        }

    @Test
    fun `should derive discount from savings, not from promotional labels`() =
        runTest {
            val dataSource =
                dataSourceRespondingWith(
                    """
                    {
                      "products": [
                        {
                          "id": "7777777777",
                          "title": "Discounted Product",
                          "url": "/nl/nl/p/discounted/7777777777/",
                          "bestSellingOffer": {
                            "sellingPrice": { "price": { "amount": "30.42" } },
                            "promotionalLabels": [ { "titleText": "deal" } ],
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
                    """.trimIndent(),
                )

            val items = dataSource.fetchItemsByEan("7777777777")

            assertEquals(1, items.size)
            val item = items.first()

            val price = item.properties.filterIsInstance<ItemProperty.Price>().first()
            assertEquals(BigDecimal("30.42"), price.value)

            val discount = item.properties.filterIsInstance<ItemProperty.Custom>().find { it.name == "Discount" }
            assertNotNull(discount)
            assertEquals("13% (was €34.99)", discount.value)
        }

    @Test
    fun `should handle multiple products in search results`() =
        runTest {
            val dataSource =
                dataSourceRespondingWith(
                    """
                    {
                      "products": [
                        {
                          "id": "1111111111",
                          "title": "Product 1",
                          "url": "/nl/nl/p/product1/1111111111/"
                        },
                        {
                          "id": "2222222222",
                          "title": "Product 2",
                          "url": "/nl/nl/p/product2/2222222222/"
                        }
                      ]
                    }
                    """.trimIndent(),
                )

            val items = dataSource.fetchItemsByEan("test")

            assertEquals(2, items.size)
            assertEquals("1111111111", items[0].id)
            assertEquals("Product 1", items[0].name)
            assertEquals("2222222222", items[1].id)
            assertEquals("Product 2", items[1].name)
        }
}
