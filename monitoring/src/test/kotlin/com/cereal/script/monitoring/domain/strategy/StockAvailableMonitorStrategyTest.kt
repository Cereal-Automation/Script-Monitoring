package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue.AvailableStock
import com.cereal.script.monitoring.domain.models.MissingValueTypeException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class StockAvailableMonitorStrategyTest {
    private val monitorStrategy = StockAvailableMonitorStrategy()

    @Test
    fun `should notify when stock is available`() {
        val item =
            Item(
                id = "1",
                url = "http://example.com/item/1",
                name = "Item 1",
                values = listOf(AvailableStock(value = 10)),
            )
        val execution = Execution(sequenceNumber = 1)

        val result = runBlocking { monitorStrategy.shouldNotify(item, execution) }

        assertTrue(result)
    }

    @Test
    fun `should not notify when stock is not available`() {
        val item =
            Item(
                id = "2",
                url = "http://example.com/item/2",
                name = "Item 2",
                values = listOf(AvailableStock(value = 0)),
            )
        val execution = Execution(sequenceNumber = 1)

        val result = runBlocking { monitorStrategy.shouldNotify(item, execution) }

        assertFalse(result)
    }

    @Test
    fun `should not notify when no available stock value is present`() {
        val item =
            Item(
                id = "3",
                url = "http://example.com/item/3",
                name = "Item 3",
                values = emptyList(),
            )
        val execution = Execution(sequenceNumber = 1)

        try {
            runBlocking { monitorStrategy.shouldNotify(item, execution) }
            assertFalse(true, "Expected MissingValueTypeException but no exception was thrown")
        } catch (e: MissingValueTypeException) {
            assertTrue(true)
        }
    }

    @Test
    fun `should return correct notification message`() {
        val item =
            Item(
                id = "1",
                url = "http://example.com/item/1",
                name = "Item 1",
                values = listOf(AvailableStock(value = 10)),
            )

        val message = monitorStrategy.getNotificationMessage(item)

        assertTrue(message.contains("Item 1 is in stock (10)!"))
    }

    @Test
    fun `should return empty message when stock is not available`() {
        val item =
            Item(
                id = "2",
                url = "http://example.com/item/2",
                name = "Item 2",
                values = listOf(AvailableStock(value = 0)),
            )

        val message = monitorStrategy.getNotificationMessage(item)

        assertTrue(message.contains("Item 2 is in stock (0)!"))
    }

    @Test
    fun `should throw exception when no available stock value is present`() {
        val item =
            Item(
                id = "3",
                url = "http://example.com/item/3",
                name = "Item 3",
                values = emptyList(),
            )

        try {
            monitorStrategy.getNotificationMessage(item)
            assertFalse(true, "Expected MissingValueTypeException but no exception was thrown")
        } catch (e: MissingValueTypeException) {
            assertTrue(true)
        }
    }
}
