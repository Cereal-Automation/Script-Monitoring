package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Variant
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StockChangedMonitorStrategyTest {
    private val strategy = StockChangedMonitorStrategy()

    @Test
    fun `item level - in stock toggle to in stock should notify with level`() {
        val previous =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = false, amount = null, level = null)),
            )
        val current =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, level = null)),
            )

        val result = runBlocking { strategy.shouldNotify(current, previous) }

        assertEquals("Item A is now in stock (3)!", result)
    }

    @Test
    fun `item level - in stock toggle to out of stock should notify`() {
        val previous =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = null, level = "HIGH")),
            )
        val current =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = false, amount = null, level = null)),
            )

        val result = runBlocking { strategy.shouldNotify(current, previous) }

        assertEquals("Item A is now out of stock", result)
    }

    @Test
    fun `item level - stock value change without toggle should notify with delta message`() {
        val previous =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 2, level = null)),
            )
        val current =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, level = null)),
            )

        val result = runBlocking { strategy.shouldNotify(current, previous) }

        assertEquals("Item A stock level changed: 2 → 5", result)
    }

    @Test
    fun `item level - no stock change should return null`() {
        val previous =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, level = null)),
            )
        val current =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, level = null)),
            )

        val result = runBlocking { strategy.shouldNotify(current, previous) }

        assertNull(result)
    }

    @Test
    fun `variant level - multiple variant changes aggregated`() {
        val prev =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                variants =
                    listOf(
                        Variant(
                            id = "v1",
                            name = "Size 8",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = false, amount = null, level = null)),
                        ),
                        Variant(
                            id = "v2",
                            name = "Size 9",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = true, amount = 2, level = null)),
                        ),
                    ),
            )
        val curr =
            Item(
                id = "1",
                url = null,
                name = "Item A",
                variants =
                    listOf(
                        Variant(
                            id = "v1",
                            name = "Size 8",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = true, amount = null, level = "LOW")),
                        ),
                        Variant(
                            id = "v2",
                            name = "Size 9",
                            styleId = null,
                            properties = listOf(ItemProperty.Stock(isInStock = true, amount = 5, level = null)),
                        ),
                    ),
            )

        val result = runBlocking { strategy.shouldNotify(curr, prev) }

        val expected =
            """
            Variant Size 8 is now in stock (LOW)
            Variant Size 9 stock level changed: 2 → 5
            """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun `requiresBaseline is false`() {
        assertEquals(false, strategy.requiresBaseline())
    }
}
