package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Variant
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StockAvailableMonitorStrategyTest {
    private val monitorStrategy = StockAvailableMonitorStrategy()
    private val monitorStrategyWithInitialRun = StockAvailableMonitorStrategy(notifyOnInitialRun = true)

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

    // Tests for initial run notification functionality

    @Test
    fun `should notify on initial run when item is in stock with notifyOnInitialRun enabled`() {
        val item = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, "HIGH")),
        )

        val result = runBlocking { monitorStrategyWithInitialRun.shouldNotify(item, null) }

        assertEquals("Test Item is in stock (5)!", result)
    }

    @Test
    fun `should not notify on initial run when item is not in stock with notifyOnInitialRun enabled`() {
        val item = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
        )

        val result = runBlocking { monitorStrategyWithInitialRun.shouldNotify(item, null) }

        assertNull(result)
    }

    @Test
    fun `should notify on initial run when variants are in stock with notifyOnInitialRun enabled`() {
        val item = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
            variants = listOf(
                Variant(
                    id = "variant1",
                    name = "Size M",
                    styleId = null,
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, "LOW")),
                ),
                Variant(
                    id = "variant2",
                    name = "Size L",
                    styleId = null,
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = null, "HIGH")),
                ),
            ),
        )

        val result = runBlocking { monitorStrategyWithInitialRun.shouldNotify(item, null) }

        assertEquals("Variant Size M is in stock: 3\nVariant Size L is in stock: HIGH", result)
    }

    @Test
    fun `should not notify on initial run when no variants are in stock with notifyOnInitialRun enabled`() {
        val item = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
            variants = listOf(
                Variant(
                    id = "variant1",
                    name = "Size M",
                    styleId = null,
                    properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
                ),
            ),
        )

        val result = runBlocking { monitorStrategyWithInitialRun.shouldNotify(item, null) }

        assertNull(result)
    }

    @Test
    fun `should not notify on initial run with default strategy (backwards compatibility)`() {
        // This test verifies that the default strategy requires a baseline
        // In practice, the monitoring system won't call shouldNotify with null previousItem
        // for strategies that require baseline, but we test the behavior directly here
        val item = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, "HIGH")),
        )

        // The default strategy should not notify on initial run even if called directly
        // This is a defensive test - in practice this won't happen due to requiresBaseline check
        val result = runBlocking { monitorStrategy.shouldNotify(item, null) }

        // The strategy should return null because it doesn't have notifyOnInitialRun enabled
        assertNull(result)
    }

    @Test
    fun `should return correct requiresBaseline value for default strategy`() {
        assert(monitorStrategy.requiresBaseline())
    }

    @Test
    fun `should return correct requiresBaseline value for initial run strategy`() {
        assert(!monitorStrategyWithInitialRun.requiresBaseline())
    }

    @Test
    fun `should handle mixed scenario with item and variants in stock on initial run`() {
        val item = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = true, amount = 2, "LOW")),
            variants = listOf(
                Variant(
                    id = "variant1",
                    name = "Size M",
                    styleId = null,
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 1, "LOW")),
                ),
            ),
        )

        val result = runBlocking { monitorStrategyWithInitialRun.shouldNotify(item, null) }

        // Should prioritize item-level notification over variant notifications
        assertEquals("Test Item is in stock (2)!", result)
    }

    @Test
    fun `should work normally with previous item when notifyOnInitialRun is enabled`() {
        val previousItem = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = false, amount = 0, "OOS")),
        )
        val currentItem = Item(
            id = "1",
            url = "http://example.com/item/1",
            name = "Test Item",
            properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, "HIGH")),
        )

        val result = runBlocking { monitorStrategyWithInitialRun.shouldNotify(currentItem, previousItem) }

        assertEquals("Test Item is in stock (5)!", result)
    }
}
