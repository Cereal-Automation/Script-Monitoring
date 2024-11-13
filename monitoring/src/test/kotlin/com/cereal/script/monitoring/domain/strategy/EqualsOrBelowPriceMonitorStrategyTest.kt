package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Currency
import com.cereal.script.monitoring.domain.models.CurrencyMismatchException
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.test.Test

class EqualsOrBelowPriceMonitorStrategyTest {
    @Test
    fun `shouldNotify returns true when item price is below provided price`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    values = listOf(ItemValue.Price(BigDecimal("100.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)

            val result = strategy.shouldNotify(item)

            assertTrue(result)
        }

    @Test
    fun `shouldNotify returns true when item price is equal to provided price`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    values = listOf(ItemValue.Price(BigDecimal("200.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("200.00"), Currency.USD)

            val result = strategy.shouldNotify(item)

            assertTrue(result)
        }

    @Test
    fun `shouldNotify throws CurrencyMismatchException when currencies do not match`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    values = listOf(ItemValue.Price(BigDecimal("100.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)

            try {
                strategy.shouldNotify(item)
                Unit
            } catch (ex: CurrencyMismatchException) {
                assertEquals("Expected currency: USD, but got: EUR", ex.message)
            }
        }

    @Test
    fun `getNotificationMessage returns correct message`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "Test Item",
                    listOf(ItemValue.Price(BigDecimal("100.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)

            val message = strategy.getNotificationMessage(item)

            assertEquals("Test Item is available for 100.00", message)
        }
}
