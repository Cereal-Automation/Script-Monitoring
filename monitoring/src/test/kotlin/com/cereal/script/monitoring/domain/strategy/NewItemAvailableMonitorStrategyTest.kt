package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.Instant
import kotlin.test.Test

class NewItemAvailableMonitorStrategyTest {
    @Test
    fun `shouldNotify returns true for item with publish date after since`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item =
            Item(
                "item1",
                "http://example.com/test",
                "Test",
                listOf(ItemValue.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )

        val execution = Execution(sequenceNumber = 1)
        val result = runBlocking { strategy.shouldNotify(item, execution) }

        assertTrue(result)
    }

    @Test
    fun `shouldNotify returns false for item with publish date before since`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item =
            Item(
                "item1",
                "http://example.com/test",
                "Test",
                listOf(ItemValue.PublishDate(Instant.parse("2022-12-31T23:59:59Z"))),
            )

        val execution = Execution(sequenceNumber = 1)
        val result = runBlocking { strategy.shouldNotify(item, execution) }

        assertFalse(result)
    }

    @Test
    fun `shouldNotify returns false for item with null publish date`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item = Item("item1", "http://example.com/test", "Test", listOf(ItemValue.PublishDate(null)))

        val execution = Execution(sequenceNumber = 1)
        val result = runBlocking { strategy.shouldNotify(item, execution) }

        assertFalse(result)
    }

    @Test
    fun `getNotificationMessage returns correct message`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item =
            Item(
                "item1",
                "http://example.com/test",
                "Test",
                listOf(ItemValue.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )

        val message = strategy.getNotificationMessage(item)

        assertEquals("Found new item: Test.", message)
    }

    @Test
    fun `shouldNotify returns false for detected item`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item =
            Item(
                "item1",
                "http://example.com/test",
                "Test",
                listOf(),
            )
        val execution = Execution(sequenceNumber = 2)

        val result1 = runBlocking { strategy.shouldNotify(item, execution) }
        val result2 = runBlocking { strategy.shouldNotify(item, execution) }

        assertTrue(result1)
        assertFalse(result2)
    }

    @Test
    fun `shouldNotify returns true for new item in sequence`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item1 =
            Item(
                "item1",
                "http://example.com/test",
                "Test1",
                listOf(ItemValue.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )
        val item2 =
            Item(
                "item2",
                "http://example.com/test2",
                "Test2",
                listOf(ItemValue.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )
        val execution = Execution(sequenceNumber = 2)

        val result1 = runBlocking { strategy.shouldNotify(item1, execution) }
        val result2 = runBlocking { strategy.shouldNotify(item2, execution) }

        assertTrue(result1)
        assertTrue(result2)
    }

    @Test
    fun `shouldNotify returns false for item in first sequence`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item =
            Item(
                "item1",
                "http://example.com/test",
                "Test",
                listOf(),
            )
        val execution = Execution(sequenceNumber = 1)

        val result = runBlocking { strategy.shouldNotify(item, execution) }

        assertFalse(result)
    }

    @Test
    fun `shouldNotify handles null publish date with non-first sequence`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item = Item("item1", "http://example.com/test", "Test", listOf(ItemValue.PublishDate(null)))
        val execution = Execution(sequenceNumber = 2)

        val result = runBlocking { strategy.shouldNotify(item, execution) }

        assertTrue(result)
    }
}
