package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PriceChangedMonitorStrategyTest {
    private val subject = PriceChangedMonitorStrategy()

    private fun itemWithPriceAndStock(
        price: BigDecimal,
        currency: Currency = Currency.EUR,
        isInStock: Boolean = true,
        amount: Int? = 3,
    ) = Item(
        id = "1",
        url = null,
        name = "Spar",
        properties =
            listOf(
                ItemProperty.Price(price, currency),
                ItemProperty.Stock(isInStock = isInStock, amount = amount, level = null),
            ),
    )

    private fun itemWithPriceOnly(
        price: BigDecimal,
        currency: Currency = Currency.EUR,
    ) = Item(
        id = "1",
        url = null,
        name = "Spar",
        properties = listOf(ItemProperty.Price(price, currency)),
    )

    @Test
    fun `returns null when previousItem is null`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, null))
        }

    @Test
    fun `returns null when price is unchanged`() =
        runBlocking {
            val item = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(item, item))
        }

    @Test
    fun `returns message with down arrow when price decreases while in stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            assertNotNull(result)
            assertTrue(result.contains("↓"), "Expected ↓ in message: $result")
            assertTrue(result.contains("Spar"), "Expected item name in message: $result")
        }

    @Test
    fun `notification message contains old price, new price, currency and direction`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            assertEquals("Price for Spar changed: 3.99 EUR → 2.99 EUR (↓)", result)
        }

    @Test
    fun `returns message with up arrow when price increases while in stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("4.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            assertNotNull(result)
            assertTrue(result.contains("↑"), "Expected ↑ in message: $result")
            assertTrue(result.contains("Spar"), "Expected item name in message: $result")
        }

    @Test
    fun `returns null when price changes but item is out of stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"), isInStock = false, amount = 0)
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns null when current item has no price`() =
        runBlocking {
            val current =
                Item(
                    id = "1",
                    url = null,
                    name = "Spar",
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, level = null)),
                )
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns null when previous item has no price`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous =
                Item(
                    id = "1",
                    url = null,
                    name = "Spar",
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, level = null)),
                )
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns null when current item has no stock property`() =
        runBlocking {
            val current = itemWithPriceOnly(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns null when currencies differ between current and previous`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"), currency = Currency.EUR)
            val previous =
                Item(
                    id = "1",
                    url = null,
                    name = "Spar",
                    properties =
                        listOf(
                            ItemProperty.Price(BigDecimal("3.99"), Currency.USD),
                            ItemProperty.Stock(isInStock = true, amount = 3, level = null),
                        ),
                )
            assertNull(subject.shouldNotify(current, previous))
        }

    @Test
    fun `requiresBaseline returns true`() {
        assertTrue(subject.requiresBaseline())
    }
}
