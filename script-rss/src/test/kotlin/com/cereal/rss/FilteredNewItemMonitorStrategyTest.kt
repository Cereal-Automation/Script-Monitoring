package com.cereal.rss

import com.cereal.command.monitor.data.rss.RssFeedItemRepository
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.strategy.MonitorStrategy
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FilteredNewItemMonitorStrategyTest {
    private val baselineStrategy: MonitorStrategy = mockk()
    private val baselineMessage = "Found new item: X."

    private fun newItem(
        name: String = "Test Item",
        description: String? = null,
        author: String? = null,
        categories: List<String> = emptyList(),
    ): Item {
        val properties = mutableListOf<ItemProperty>()
        if (author != null) properties.add(ItemProperty.Custom(RssFeedItemRepository.PROPERTY_AUTHOR, author))
        categories.forEach { properties.add(ItemProperty.Custom(RssFeedItemRepository.PROPERTY_CATEGORY, it)) }
        return Item(id = "1", url = "http://example.com", name = name, description = description, properties = properties)
    }

    // --- No filters configured ---

    @Test
    fun `no filters configured passes through baseline message`() =
        runTest {
            val item = newItem()
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = emptyList(),
                    authors = emptyList(),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    // --- Baseline returns null ---

    @Test
    fun `baseline returns null always returns null regardless of filters`() =
        runTest {
            val item = newItem(name = "Kotlin Tutorial", author = "Alice", categories = listOf("tech"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns null

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("Kotlin"),
                    authors = listOf("Alice"),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertNull(result)
        }

    // --- MATCH_ANY: keyword matches ---

    @Test
    fun `MATCH_ANY keyword matches returns baseline message`() =
        runTest {
            val item = newItem(name = "Kotlin Tutorial")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("Kotlin"),
                    authors = emptyList(),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    // --- MATCH_ANY: author matches ---

    @Test
    fun `MATCH_ANY author matches returns baseline message`() =
        runTest {
            val item = newItem(name = "Some Post", author = "Alice")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("NonMatchingKeyword"),
                    authors = listOf("Alice"),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    // --- MATCH_ANY: category matches ---

    @Test
    fun `MATCH_ANY category matches returns baseline message`() =
        runTest {
            val item = newItem(name = "Some Post", categories = listOf("tech"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("NonMatchingKeyword"),
                    authors = listOf("NonMatchingAuthor"),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    // --- MATCH_ANY: nothing matches ---

    @Test
    fun `MATCH_ANY nothing matches returns null`() =
        runTest {
            val item = newItem(name = "Java Tutorial", author = "Bob", categories = listOf("general"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("Kotlin"),
                    authors = listOf("Alice"),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertNull(result)
        }

    // --- MATCH_ALL: all filters match ---

    @Test
    fun `MATCH_ALL all filters match returns baseline message`() =
        runTest {
            val item = newItem(name = "Kotlin Tutorial", author = "Alice", categories = listOf("tech"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("Kotlin"),
                    authors = listOf("Alice"),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    // --- MATCH_ALL: only keyword matches (missing author) ---

    @Test
    fun `MATCH_ALL only keyword matches missing author returns null`() =
        runTest {
            val item = newItem(name = "Kotlin Tutorial", author = "Bob", categories = listOf("tech"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("Kotlin"),
                    authors = listOf("Alice"),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertNull(result)
        }

    // --- MATCH_ALL: only keywords configured, keyword matches ---

    @Test
    fun `MATCH_ALL only keywords configured keyword matches returns baseline message`() =
        runTest {
            val item = newItem(name = "Kotlin Tutorial")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("Kotlin"),
                    authors = emptyList(),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    @Test
    fun `MATCH_ALL only keywords configured keyword does not match returns null`() =
        runTest {
            val item = newItem(name = "Java Tutorial")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("Kotlin"),
                    authors = emptyList(),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertNull(result)
        }

    // --- Case-insensitive matching ---

    @Test
    fun `keyword matching is case-insensitive`() =
        runTest {
            val item = newItem(name = "KOTLIN tutorial")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("kotlin"),
                    authors = emptyList(),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    @Test
    fun `keyword matching in description is case-insensitive`() =
        runTest {
            val item = newItem(name = "Some Post", description = "KOTLIN programming")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = listOf("kotlin"),
                    authors = emptyList(),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    @Test
    fun `author matching is case-insensitive`() =
        runTest {
            val item = newItem(name = "Some Post", author = "ALICE")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = emptyList(),
                    authors = listOf("alice"),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    @Test
    fun `category matching is case-insensitive`() =
        runTest {
            val item = newItem(name = "Some Post", categories = listOf("TECH"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = emptyList(),
                    authors = emptyList(),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ANY,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    // --- MATCH_ALL: only authors configured ---

    @Test
    fun `MATCH_ALL only authors configured author matches returns baseline message`() =
        runTest {
            val item = newItem(author = "Jane Doe")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = emptyList(),
                    authors = listOf("Jane Doe"),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    @Test
    fun `MATCH_ALL only authors configured author does not match returns null`() =
        runTest {
            val item = newItem(author = "Other Author")
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = emptyList(),
                    authors = listOf("Jane Doe"),
                    categories = emptyList(),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertNull(result)
        }

    // --- MATCH_ALL: only categories configured ---

    @Test
    fun `MATCH_ALL only categories configured category matches returns baseline message`() =
        runTest {
            val item = newItem(categories = listOf("tech"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = emptyList(),
                    authors = emptyList(),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertEquals(baselineMessage, result)
        }

    @Test
    fun `MATCH_ALL only categories configured category does not match returns null`() =
        runTest {
            val item = newItem(categories = listOf("sports"))
            coEvery { baselineStrategy.shouldNotify(item, null) } returns baselineMessage

            val strategy =
                FilteredNewItemMonitorStrategy(
                    baselineStrategy = baselineStrategy,
                    keywords = emptyList(),
                    authors = emptyList(),
                    categories = listOf("tech"),
                    logic = FilterLogic.MATCH_ALL,
                )

            val result = strategy.shouldNotify(item, null)
            assertNull(result)
        }

    // --- requiresBaseline delegates ---

    @Test
    fun `requiresBaseline delegates to baseline strategy`() {
        val strategy =
            FilteredNewItemMonitorStrategy(
                baselineStrategy = baselineStrategy,
                keywords = emptyList(),
                authors = emptyList(),
                categories = emptyList(),
                logic = FilterLogic.MATCH_ALL,
            )

        every { baselineStrategy.requiresBaseline() } returns true
        assertEquals(true, strategy.requiresBaseline())

        every { baselineStrategy.requiresBaseline() } returns false
        assertEquals(false, strategy.requiresBaseline())
    }
}
