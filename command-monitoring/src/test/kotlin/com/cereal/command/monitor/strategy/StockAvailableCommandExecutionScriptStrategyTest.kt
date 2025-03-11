package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.ItemProperty.AvailableStock
import com.cereal.command.monitor.models.Variant
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StockAvailableCommandExecutionScriptStrategyTest {
    private val monitorStrategy = StockAvailableMonitorStrategy()

    @Test
    fun `should notify when stock is available`() {
        val item =
            Item(
                id = "1",
                url = "http://example.com/item/1",
                name = "Item 1",
                properties = listOf(AvailableStock(value = 10)),
            )
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com/item/1",
                name = "Item 1",
                properties = listOf(AvailableStock(value = 0)),
            )
        val result = runBlocking { monitorStrategy.shouldNotify(item, previousItem) }

        assertNotNull(result)
    }

    @Test
    fun `should not notify when stock is not available`() {
        val item =
            Item(
                id = "2",
                url = "http://example.com/item/2",
                name = "Item 2",
                properties = listOf(AvailableStock(value = 0)),
            )
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com/item/1",
                name = "Item 1",
                properties = listOf(AvailableStock(value = 10)),
            )
        val result = runBlocking { monitorStrategy.shouldNotify(item, previousItem) }

        assertNull(result)
    }

    @Test
    fun `should notify when stock becomes available for first time`() {
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties = listOf(AvailableStock(0)),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties = listOf(AvailableStock(5)),
            )

        val result = runBlocking { monitorStrategy.shouldNotify(currentItem, previousItem) }

        assertEquals("Test Item is in stock (5)!", result)
    }

    @Test
    fun `should return null when stock remains unavailable`() {
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties = listOf(AvailableStock(0)),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties = listOf(AvailableStock(0)),
            )

        val result = runBlocking { monitorStrategy.shouldNotify(currentItem, previousItem) }

        assertNull(result)
    }

    @Test
    fun `should notify about new variants in stock`() {
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties =
                    listOf(
                        ItemProperty.Variants(
                            value =
                                listOf(
                                    Variant("variant1", inStock = false, stockLevel = "OOS"),
                                ),
                        ),
                    ),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties =
                    listOf(
                        ItemProperty.Variants(
                            value =
                                listOf(
                                    Variant("variant1", inStock = false, stockLevel = "OOS"),
                                    Variant("variant2", inStock = true, stockLevel = "HIGH"),
                                ),
                        ),
                    ),
            )

        val result = runBlocking { monitorStrategy.shouldNotify(currentItem, previousItem) }

        assertEquals("New variant variant2 is in stock: HIGH", result)
    }

    @Test
    fun `should notify when variants become in stock`() {
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties =
                    listOf(
                        ItemProperty.Variants(
                            value =
                                listOf(
                                    Variant("variant1", inStock = false, stockLevel = "OOS"),
                                ),
                        ),
                    ),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties =
                    listOf(
                        ItemProperty.Variants(
                            value =
                                listOf(
                                    Variant("variant1", inStock = true, stockLevel = "HIGH"),
                                ),
                        ),
                    ),
            )

        val result = runBlocking { monitorStrategy.shouldNotify(currentItem, previousItem) }

        assertEquals("Variant variant1 is in stock: HIGH", result)
    }

    @Test
    fun `should return null when no relevant changes`() {
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties =
                    listOf(
                        ItemProperty.Variants(
                            value =
                                listOf(
                                    Variant("variant1", inStock = true, stockLevel = "HIGH"),
                                ),
                        ),
                    ),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties =
                    listOf(
                        ItemProperty.Variants(
                            value =
                                listOf(
                                    Variant("variant1", inStock = true, stockLevel = "HIGH"),
                                ),
                        ),
                    ),
            )

        val result = runBlocking { monitorStrategy.shouldNotify(currentItem, previousItem) }

        assertNull(result)
    }
}
