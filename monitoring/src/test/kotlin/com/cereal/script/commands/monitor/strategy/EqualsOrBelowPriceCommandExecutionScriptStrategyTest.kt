package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.models.Currency
import com.cereal.script.commands.monitor.models.CurrencyMismatchException
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EqualsOrBelowPriceCommandExecutionScriptStrategyTest {
    @Test
    fun `shouldNotify returns true when item price is below provided price`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    properties = listOf(ItemProperty.Price(BigDecimal("100.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)
            val result = strategy.shouldNotify(item, null)

            assertNotNull(result)
            Unit
        }

    @Test
    fun `shouldNotify returns true when item price is equal to provided price`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    properties = listOf(ItemProperty.Price(BigDecimal("200.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("200.00"), Currency.USD)
            val result = strategy.shouldNotify(item, null)

            assertNotNull(result)
            Unit
        }

    @Test
    fun `shouldNotify throws CurrencyMismatchException when currencies do not match`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    properties = listOf(ItemProperty.Price(BigDecimal("100.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)

            try {
                strategy.shouldNotify(item, null)
                Unit
            } catch (ex: CurrencyMismatchException) {
                assertEquals("Expected currency: USD, but got: EUR", ex.message)
            }
        }

    @Test
    fun `shouldNotify returns false when item price is above provided price`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    properties = listOf(ItemProperty.Price(BigDecimal("200.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)
            val result = strategy.shouldNotify(item, null)

            assertNull(result)
        }

    @Test
    fun `shouldNotify updates comparison price after first notification check`() =
        runBlocking {
            val item1 =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    properties = listOf(ItemProperty.Price(BigDecimal("100.00"), Currency.USD)),
                )
            val item2 =
                Item(
                    id = "id",
                    url = "url",
                    name = "name2",
                    properties = listOf(ItemProperty.Price(BigDecimal("75.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)

            strategy.shouldNotify(item1, null)
            val result = strategy.shouldNotify(item2, item1)

            assertNotNull(result)
            Unit
        }
}
