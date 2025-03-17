package com.cereal.command.monitor.data

import com.cereal.command.monitor.data.nike.NikeItemRepository
import com.cereal.command.monitor.data.nike.ScrapeCategory
import com.cereal.command.monitor.data.shopify.ShopifyItemRepository
import com.cereal.command.monitor.data.shopify.ShopifyWebsite
import com.cereal.command.monitor.data.snkrs.Locale
import com.cereal.command.monitor.data.snkrs.SnkrsApiClient
import com.cereal.command.monitor.data.snkrs.SnkrsItemRepository
import com.cereal.command.monitor.fixtures.FakeLogRepository
import com.cereal.command.monitor.repository.ItemRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertNotNull

class TestItemRepositoryIntegrations {
    @Tag("integration")
    @ParameterizedTest
    @MethodSource("data")
    fun testGetItems(repository: ItemRepository) =
        runBlocking {
            // Run without a next page token.
            val result = repository.getItems(null)
            assertNotNull(result)

            // Run with a next page token (if provided).
            result.nextPageToken?.let {
                val result2 = repository.getItems(it)
                assertNotNull(result2)
            }

            Unit
        }

    companion object {
        @JvmStatic
        fun data(): List<ItemRepository> =
            listOf(
                NikeItemRepository(
                    FakeLogRepository(),
                    ScrapeCategory.MEN_ALL_SHOES,
                ),
                SnkrsItemRepository(
                    SnkrsApiClient(FakeLogRepository(), null),
                    Locale.BE_NL,
                ),
                ShopifyItemRepository(FakeLogRepository(), ShopifyWebsite("Test", "https://bdgastore.com/collections/newarrivals")),
            )
    }
}
