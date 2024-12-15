package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Currency
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceDropCommandExecutionScriptStrategyTest {
    private val subject = PriceDropMonitorStrategy()

    @Test
    fun `shouldNotify returns false if price was not changed`() =
        runBlocking {
            val price = ItemProperty.Price(BigDecimal(100), Currency.USD)
            val previousPrice = ItemProperty.Price(BigDecimal(100), Currency.USD)
            val item = Item("1", "url", "item", properties = listOf(price))
            val previousItem = Item("1", "url", "item", properties = listOf(previousPrice))

            // First call to shouldNotify should return false as there is no previous price to check against
            assertFalse(subject.shouldNotify(item, null))
            // Second call with the same price should return false as price has not dropped
            assertFalse(subject.shouldNotify(item, previousItem))
        }

    @Test
    fun `shouldNotify returns true if price was decreased`() =
        runBlocking {
            val initialPrice = ItemProperty.Price(BigDecimal(100), Currency.USD)
            val item1 = Item("1", "url", "item", properties = listOf(initialPrice))
            val previousItem = Item("1", "url", "item", properties = listOf(initialPrice))

            // initialize item with original price
            assertFalse(subject.shouldNotify(item1, previousItem))

            val previousPrice = ItemProperty.Price(BigDecimal(150), Currency.USD)
            val item2 = Item("1", "url", "item", properties = listOf(previousPrice))

            // after price decrease shouldNotify should return true
            assertTrue(subject.shouldNotify(item1, item2))
        }

    @Test
    fun `shouldNotify returns false if price was increased`() =
        runBlocking {
            val initialPrice = ItemProperty.Price(BigDecimal(100), Currency.USD)
            val item1 = Item("1", "url", "item", properties = listOf(initialPrice))
            val previousItem = Item("1", "url", "item", properties = listOf(initialPrice))

            // initialize item with original price
            assertFalse(subject.shouldNotify(item1, previousItem))

            val increasedPrice = ItemProperty.Price(BigDecimal(150), Currency.USD)
            val item2 = Item("1", "url", "item", properties = listOf(increasedPrice))

            // after price increase shouldNotify should return false
            assertFalse(subject.shouldNotify(item2, item1))
        }
}
