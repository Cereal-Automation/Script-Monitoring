package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
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

        val result = runBlocking { strategy.shouldNotify(item) }

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

        val result = runBlocking { strategy.shouldNotify(item) }

        assertFalse(result)
    }

    @Test
    fun `shouldNotify returns false for item with null publish date`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item = Item("item1", "http://example.com/test", "Test", listOf(ItemValue.PublishDate(null)))

        val result = runBlocking { strategy.shouldNotify(item) }

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
}
