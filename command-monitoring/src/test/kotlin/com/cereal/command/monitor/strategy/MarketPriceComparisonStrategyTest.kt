package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.MarketItem
import com.cereal.command.monitor.models.MarketItemVariant
import com.cereal.command.monitor.models.Variant
import com.cereal.command.monitor.repository.MarketItemRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MarketPriceComparisonStrategyTest {

    private lateinit var marketItemRepository: MarketItemRepository
    private lateinit var strategy: MarketPriceComparisonStrategy

    @BeforeEach
    fun setup() {
        marketItemRepository = mockk()
        strategy = MarketPriceComparisonStrategy(marketItemRepository)
    }

    @Test
    fun `shouldNotify returns null when no variants with styleId exist`() = runBlocking {
        // Prepare item with no styleIds
        val item = Item(
            id = "item1",
            url = "https://example.com/item1",
            name = "Test Item",
            variants = listOf(
                Variant(
                    id = "variant1",
                    name = "Test Variant",
                    styleId = null,
                    properties = listOf(
                        ItemProperty.Price(BigDecimal("100.00"), Currency.USD)
                    )
                )
            )
        )

        // Execute
        val result = strategy.shouldNotify(item, null)

        // Verify
        assertNull(result)
    }

    @Test
    fun `shouldNotify returns null when no matching market item is found`() = runBlocking {
        // Prepare
        val item = createItemWithStyleId("style123", BigDecimal("100.00"))

        // Mock repository to return null (no market item found)
        coEvery { marketItemRepository.search(match { it.styleId == "style123" }) } returns null

        // Execute
        val result = strategy.shouldNotify(item, null)

        // Verify
        assertNull(result)
    }

    @Test
    fun `shouldNotify returns null when market price is equal to item price`() = runBlocking {
        // Prepare
        val itemPrice = BigDecimal("100.00")
        val marketPrice = BigDecimal("100.00")
        val item = createItemWithStyleId("style123", itemPrice)
        val marketItem = createMarketItem("style123", marketPrice)

        // Mock repository
        coEvery { marketItemRepository.search(match { it.styleId == "style123" }) } returns marketItem

        // Execute
        val result = strategy.shouldNotify(item, null)

        // Verify
        assertNull(result)
    }

    @Test
    fun `shouldNotify returns null when market price is lower than item price`() = runBlocking {
        // Prepare
        val itemPrice = BigDecimal("100.00")
        val marketPrice = BigDecimal("90.00")
        val item = createItemWithStyleId("style123", itemPrice)
        val marketItem = createMarketItem("style123", marketPrice)

        // Mock repository
        coEvery { marketItemRepository.search(match { it.styleId == "style123" }) } returns marketItem

        // Execute
        val result = strategy.shouldNotify(item, null)

        // Verify
        assertNull(result)
    }

    @Test
    fun `shouldNotify returns null when price difference is below threshold`() = runBlocking {
        // Prepare
        val itemPrice = BigDecimal("95.00")
        val marketPrice = BigDecimal("100.00")  // Only $5 difference, below threshold
        val item = createItemWithStyleId("style123", itemPrice)
        val marketItem = createMarketItem("style123", marketPrice)

        // Mock repository
        coEvery { marketItemRepository.search(match { it.styleId == "style123" }) } returns marketItem

        // Execute
        val result = strategy.shouldNotify(item, null)

        // Verify
        assertNull(result)
    }

    @Test
    fun `shouldNotify returns notification when price difference is above threshold`() = runBlocking {
        // Prepare
        val itemPrice = BigDecimal("90.00")
        val marketPrice = BigDecimal("110.00")  // $20 difference, above threshold
        val item = createItemWithStyleId("style123", itemPrice)
        val marketItem = createMarketItem("style123", marketPrice)

        // Mock repository
        coEvery { marketItemRepository.search(match { it.styleId == "style123" }) } returns marketItem

        // Execute
        val result = strategy.shouldNotify(item, null)

        // Verify
        assertNotNull(result)
        assertTrue(result.contains("18,2%"))
        assertTrue(result.contains("You'll earn"))
    }

    @Test
    fun `shouldNotify returns notification for multiple variants with different prices`() = runBlocking {
        // Prepare
        val item = Item(
            id = "item1",
            url = "https://example.com/item1",
            name = "Test Item",
            variants = listOf(
                Variant(
                    id = "variant1",
                    name = "Variant 1",
                    styleId = "style123",
                    properties = listOf(
                        ItemProperty.Price(BigDecimal("80.00"), Currency.USD),
                        ItemProperty.Size(
                            ItemProperty.Size.SizeType.US_MEN,
                            ItemProperty.Size.SizeValue.US_MEN_9
                        )
                    )
                ),
                Variant(
                    id = "variant2",
                    name = "Variant 2",
                    styleId = "style456",
                    properties = listOf(
                        ItemProperty.Price(BigDecimal("70.00"), Currency.USD),
                        ItemProperty.Size(
                            ItemProperty.Size.SizeType.US_MEN,
                            ItemProperty.Size.SizeValue.US_MEN_10
                        )
                    )
                )
            )
        )

        // Create market items with higher prices
        val marketItem1 = MarketItem(
            id = "market1",
            url = "https://market.com/item1",
            variants = listOf(
                MarketItemVariant(
                    id = "mv1",
                    properties = listOf(
                        ItemProperty.Price(BigDecimal("100.00"), Currency.USD),
                        ItemProperty.Size(
                            ItemProperty.Size.SizeType.US_MEN,
                            ItemProperty.Size.SizeValue.US_MEN_9
                        )
                    )
                )
            )
        )

        val marketItem2 = MarketItem(
            id = "market2",
            url = "https://market.com/item2",
            variants = listOf(
                MarketItemVariant(
                    id = "mv2",
                    properties = listOf(
                        ItemProperty.Price(BigDecimal("90.00"), Currency.USD),
                        ItemProperty.Size(
                            ItemProperty.Size.SizeType.US_MEN,
                            ItemProperty.Size.SizeValue.US_MEN_10
                        )
                    )
                )
            )
        )

        // Mock repository to return different market items based on style ID
        coEvery { marketItemRepository.search(match { it.styleId == "style123" }) } returns marketItem1
        coEvery { marketItemRepository.search(match { it.styleId == "style456" }) } returns marketItem2

        // Execute
        val result = strategy.shouldNotify(item, null)

        // Verify
        assertNotNull(result)
        assertTrue(result!!.contains("Variant 1"))
        assertTrue(result.contains("Variant 2"))
        assertTrue(result.contains("You'll earn"))
        assertTrue(result.contains("You'll earn"))
    }

    @Test
    fun `requiresBaseline returns false`() {
        assertEquals(false, strategy.requiresBaseline())
    }

    // Helper methods to create test data
    private fun createItemWithStyleId(styleId: String, price: BigDecimal): Item {
        return Item(
            id = "item1",
            url = "https://example.com/item1",
            name = "Test Item",
            variants = listOf(
                Variant(
                    id = "variant1",
                    name = "Test Variant",
                    styleId = styleId,
                    properties = listOf(
                        ItemProperty.Price(price, Currency.USD),
                        ItemProperty.Size(
                            ItemProperty.Size.SizeType.US_MEN,
                            ItemProperty.Size.SizeValue.US_MEN_9
                        )
                    )
                )
            )
        )
    }

    private fun createMarketItem(styleId: String, price: BigDecimal): MarketItem {
        return MarketItem(
            id = "market1",
            url = "https://market.com/item1",
            variants = listOf(
                MarketItemVariant(
                    id = "mv1",
                    properties = listOf(
                        ItemProperty.Price(price, Currency.USD),
                        ItemProperty.Size(
                            ItemProperty.Size.SizeType.US_MEN,
                            ItemProperty.Size.SizeValue.US_MEN_9
                        )
                    )
                )
            )
        )
    }
}