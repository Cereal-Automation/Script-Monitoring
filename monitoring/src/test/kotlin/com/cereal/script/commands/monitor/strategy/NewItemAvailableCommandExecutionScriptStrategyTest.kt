package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemProperty
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.Instant
import kotlin.test.Test

class NewItemAvailableCommandExecutionScriptStrategyTest {
    @Test
    fun `shouldNotify returns true for item with publish date after since`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item =
            Item(
                "item1",
                "http://example.com/test",
                "Test",
                listOf(ItemProperty.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )

        val result = runBlocking { strategy.shouldNotify(item, 1) }

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
                listOf(ItemProperty.PublishDate(Instant.parse("2022-12-31T23:59:59Z"))),
            )

        val result = runBlocking { strategy.shouldNotify(item, 1) }

        assertFalse(result)
    }

    @Test
    fun `shouldNotify returns false for item with null publish date`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item = Item("item1", "http://example.com/test", "Test", listOf(ItemProperty.PublishDate(null)))

        val result = runBlocking { strategy.shouldNotify(item, 1) }

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
                listOf(ItemProperty.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
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

        val result1 = runBlocking { strategy.shouldNotify(item, 2) }
        val result2 = runBlocking { strategy.shouldNotify(item, 3) }

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
                listOf(ItemProperty.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )
        val item2 =
            Item(
                "item2",
                "http://example.com/test2",
                "Test2",
                listOf(ItemProperty.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )

        val result1 = runBlocking { strategy.shouldNotify(item1, 1) }
        val result2 = runBlocking { strategy.shouldNotify(item2, 2) }

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

        val result = runBlocking { strategy.shouldNotify(item, 1) }

        assertFalse(result)
    }

    @Test
    fun `shouldNotify handles null publish date with non-first sequence`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item = Item("item1", "http://example.com/test", "Test", listOf(ItemProperty.PublishDate(null)))

        val result = runBlocking { strategy.shouldNotify(item, 2) }

        assertTrue(result)
    }
}
