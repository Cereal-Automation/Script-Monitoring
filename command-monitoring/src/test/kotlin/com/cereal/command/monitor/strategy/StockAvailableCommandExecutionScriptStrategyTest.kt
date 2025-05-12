package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
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
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 10, null)),
            )
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com/item/1",
                name = "Item 1",
                properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, null)),
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
                properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, null)),
            )
        val previousItem =
            Item(
                id = "1",
                url = "http://example.com/item/1",
                name = "Item 1",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 10, null)),
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
                properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, null)),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, null)),
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
                properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, null)),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, null)),
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
                variants =
                    listOf(
                        Variant(
                            id = "variant1",
                            name = "variant1",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
                        ),
                    ),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                variants =
                    listOf(
                        Variant(
                            "1",
                            "variant1",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
                        ),
                        Variant(
                            "2",
                            "variant2",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = true, amount = null, "HIGH")),
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
                variants =
                    listOf(
                        Variant(
                            "1",
                            "variant1",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
                        ),
                    ),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                variants =
                    listOf(
                        Variant(
                            "1",
                            "variant1",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = true, amount = null, "HIGH")),
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
                variants =
                    listOf(
                        Variant(
                            "2",
                            "variant2",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = true, amount = null, "HIGH")),
                        ),
                    ),
            )
        val currentItem =
            Item(
                id = "1",
                url = "http://example.com",
                name = "Test Item",
                variants =
                    listOf(
                        Variant(
                            "2",
                            "variant2",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = true, amount = null, "HIGH")),
                        ),
                    ),
            )

        val result = runBlocking { monitorStrategy.shouldNotify(currentItem, previousItem) }

        assertNull(result)
    }
}
