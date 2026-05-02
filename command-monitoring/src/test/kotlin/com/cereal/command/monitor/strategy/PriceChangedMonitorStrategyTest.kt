package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
    fun `returns Skip when previousItem is null`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("3.99"))
            assertIs<MonitorStrategy.NotifyResult.Skip>(subject.shouldNotify(current, null))
        }

    @Test
    fun `returns Skip when price is unchanged`() =
        runBlocking {
            val item = itemWithPriceAndStock(BigDecimal("3.99"))
            assertIs<MonitorStrategy.NotifyResult.Skip>(subject.shouldNotify(item, item))
        }

    @Test
    fun `returns Notify with down arrow when price decreases while in stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            val notify = assertIs<MonitorStrategy.NotifyResult.Notify>(result)
            assert(notify.message.contains("↓")) { "Expected ↓ in message: ${notify.message}" }
            assert(notify.message.contains("Spar")) { "Expected item name in message: ${notify.message}" }
        }

    @Test
    fun `notification message contains old price, new price, currency and direction`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            val notify = assertIs<MonitorStrategy.NotifyResult.Notify>(result)
            assertEquals("Price for Spar changed: 3.99 EUR → 2.99 EUR (↓)", notify.message)
        }

    @Test
    fun `returns Notify with up arrow when price increases while in stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("4.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            val result = subject.shouldNotify(current, previous)
            val notify = assertIs<MonitorStrategy.NotifyResult.Notify>(result)
            assert(notify.message.contains("↑")) { "Expected ↑ in message: ${notify.message}" }
            assert(notify.message.contains("Spar")) { "Expected item name in message: ${notify.message}" }
        }

    @Test
    fun `returns Skip when price changes but item is out of stock`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"), isInStock = false, amount = 0)
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertIs<MonitorStrategy.NotifyResult.Skip>(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns Skip when current item has no price`() =
        runBlocking {
            val current =
                Item(
                    id = "1",
                    url = null,
                    name = "Spar",
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, level = null)),
                )
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertIs<MonitorStrategy.NotifyResult.Skip>(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns Skip when previous item has no price`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous =
                Item(
                    id = "1",
                    url = null,
                    name = "Spar",
                    properties = listOf(ItemProperty.Stock(isInStock = true, amount = 3, level = null)),
                )
            assertIs<MonitorStrategy.NotifyResult.Skip>(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns Skip when current item has no stock property`() =
        runBlocking {
            val current = itemWithPriceOnly(BigDecimal("2.99"))
            val previous = itemWithPriceAndStock(BigDecimal("3.99"))
            assertIs<MonitorStrategy.NotifyResult.Skip>(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns Skip when currencies differ between current and previous`() =
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
            assertIs<MonitorStrategy.NotifyResult.Skip>(subject.shouldNotify(current, previous))
        }

    @Test
    fun `returns Notify when previous item has no stock property but current item is in stock and price changed`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("2.99"))
            val previous = itemWithPriceOnly(BigDecimal("3.99"))
            assertIs<MonitorStrategy.NotifyResult.Notify>(subject.shouldNotify(current, previous))
        }

    @Test
    fun `notification message contains old price, new price, currency and up arrow for price increase`() =
        runBlocking {
            val current = itemWithPriceAndStock(BigDecimal("3.99"))
            val previous = itemWithPriceAndStock(BigDecimal("2.99"))
            val result = subject.shouldNotify(current, previous)
            val notify = assertIs<MonitorStrategy.NotifyResult.Notify>(result)
            assertEquals("Price for Spar changed: 2.99 EUR → 3.99 EUR (↑)", notify.message)
        }

    @Test
    fun `requiresBaseline returns true`() {
        assert(subject.requiresBaseline())
    }
}
