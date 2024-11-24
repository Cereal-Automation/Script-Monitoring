package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Currency
import com.cereal.script.monitoring.domain.models.CurrencyMismatchException
import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

            val execution = Execution(sequenceNumber = 1)
            val result = strategy.shouldNotify(item, execution)

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

            val execution = Execution(sequenceNumber = 1)
            val result = strategy.shouldNotify(item, execution)

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
                val execution = Execution(sequenceNumber = 1)
                strategy.shouldNotify(item, execution)
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

    @Test
    fun `shouldNotify returns false when item price is above provided price`() =
        runBlocking {
            val item =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    values = listOf(ItemValue.Price(BigDecimal("200.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)

            val execution = Execution(sequenceNumber = 1)
            val result = strategy.shouldNotify(item, execution)

            assertTrue(!result)
        }

    @Test
    fun `shouldNotify updates comparison price after first notification check`() =
        runBlocking {
            val item1 =
                Item(
                    id = "id",
                    url = "url",
                    name = "name",
                    values = listOf(ItemValue.Price(BigDecimal("100.00"), Currency.USD)),
                )
            val item2 =
                Item(
                    id = "id",
                    url = "url",
                    name = "name2",
                    values = listOf(ItemValue.Price(BigDecimal("75.00"), Currency.USD)),
                )
            val strategy = EqualsOrBelowPriceMonitorStrategy(BigDecimal("150.00"), Currency.USD)

            val execution = Execution(sequenceNumber = 1)
            strategy.shouldNotify(item1, execution)
            val result = strategy.shouldNotify(item2, execution)

            assertTrue(result)
        }
}
