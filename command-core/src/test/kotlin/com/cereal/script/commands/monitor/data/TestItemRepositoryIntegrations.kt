package com.cereal.script.commands.monitor.data

import com.cereal.script.commands.monitor.data.nike.NikeItemRepository
import com.cereal.script.commands.monitor.data.nike.ScrapeCategory
import com.cereal.script.commands.monitor.data.snkrs.Locale
import com.cereal.script.commands.monitor.data.snkrs.SnkrsApiClient
import com.cereal.script.commands.monitor.data.snkrs.SnkrsItemRepository
import com.cereal.script.commands.monitor.repository.ItemRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TestItemRepositoryIntegrations {
    @Tag("integration")
    @ParameterizedTest
    @MethodSource("data")
    fun testGetItems(repository: ItemRepository) =
        runBlocking {
            // Run without a next page token.
            val result = repository.getItems(null)
            assert(result.items.isNotEmpty())

            // Run with a next page token (if provided).
            result.nextPageToken?.let {
                val result2 = repository.getItems(it)
                assert(result2.items.isNotEmpty())
            }

            Unit
        }

    companion object {
        @JvmStatic
        fun data(): List<ItemRepository> =
            listOf(
                NikeItemRepository(
                    ScrapeCategory.MEN_ALL_SHOES,
                    null,
                ),
                SnkrsItemRepository(
                    SnkrsApiClient(null),
                    Locale.BE_NL,
                ),
            )
    }
}
