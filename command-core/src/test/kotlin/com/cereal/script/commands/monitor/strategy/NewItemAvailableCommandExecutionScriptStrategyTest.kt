package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
                properties = listOf(ItemProperty.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )

        val result = runBlocking { strategy.shouldNotify(item, null) }

        assertNotNull(result)
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
                properties = listOf(ItemProperty.PublishDate(Instant.parse("2022-12-31T23:59:59Z"))),
            )

        val result = runBlocking { strategy.shouldNotify(item, null) }

        assertNull(result)
    }

    @Test
    fun `shouldNotify returns false for item with null publish date`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item = Item("item1", "http://example.com/test", "Test", properties = listOf(ItemProperty.PublishDate(null)))
        val previousItem = Item("item1", "http://example.com/test", "Test", properties = listOf())

        val result = runBlocking { strategy.shouldNotify(item, previousItem) }

        assertNull(result)
    }

    @Test
    fun `shouldNotify returns false when there's a previous item`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item =
            Item(
                "item1",
                "http://example.com/test",
                "Test",
                properties = listOf(),
            )
        val previousItem =
            Item(
                "item2",
                "http://example.com/test",
                "Test",
                properties = listOf(),
            )

        val result1 = runBlocking { strategy.shouldNotify(item, null) }
        val result2 = runBlocking { strategy.shouldNotify(item, previousItem) }

        assertNotNull(result1)
        assertNull(result2)
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
                properties = listOf(ItemProperty.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )
        val item2 =
            Item(
                "item2",
                "http://example.com/test2",
                "Test2",
                properties = listOf(ItemProperty.PublishDate(Instant.parse("2023-01-02T00:00:00Z"))),
            )

        val result1 = runBlocking { strategy.shouldNotify(item1, null) }
        val result2 = runBlocking { strategy.shouldNotify(item2, null) }

        assertNotNull(result1)
        assertNotNull(result2)
    }

    @Test
    fun `shouldNotify handles null publish date with non-first sequence`() {
        val since = Instant.parse("2023-01-01T00:00:00Z")
        val strategy = NewItemAvailableMonitorStrategy(since)
        val item = Item("item1", "http://example.com/test", "Test", properties = listOf(ItemProperty.PublishDate(null)))

        val result = runBlocking { strategy.shouldNotify(item, null) }

        assertNotNull(result)
    }
}
