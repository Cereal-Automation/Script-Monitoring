package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Currency
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemValue
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
            val initialPrice = ItemValue.Price(BigDecimal(100), Currency.USD)
            val item = Item("1", "url", "item", listOf(initialPrice))

            // First call to shouldNotify should return false as there is no previous price to check against
            assertFalse(subject.shouldNotify(item, 1))
            // Second call with the same price should return false as price has not dropped
            assertFalse(subject.shouldNotify(item, 2))
        }

    @Test
    fun `shouldNotify returns true if price was decreased`() =
        runBlocking {
            val initialPrice = ItemValue.Price(BigDecimal(100), Currency.USD)
            val item1 = Item("1", "url", "item", listOf(initialPrice))

            // initialize item with original price
            assertFalse(subject.shouldNotify(item1, 1))

            val decreasedPrice = ItemValue.Price(BigDecimal(50), Currency.USD)
            val item2 = Item("1", "url", "item", listOf(decreasedPrice))

            // after price decrease shouldNotify should return true
            assertTrue(subject.shouldNotify(item2, 2))
        }

    @Test
    fun `shouldNotify returns false if price was increased`() =
        runBlocking {
            val initialPrice = ItemValue.Price(BigDecimal(100), Currency.USD)
            val item1 = Item("1", "url", "item", listOf(initialPrice))

            // initialize item with original price
            assertFalse(subject.shouldNotify(item1, 1))

            val increasedPrice = ItemValue.Price(BigDecimal(150), Currency.USD)
            val item2 = Item("1", "url", "item", listOf(increasedPrice))

            // after price increase shouldNotify should return false
            assertFalse(subject.shouldNotify(item2, 2))
        }
}
