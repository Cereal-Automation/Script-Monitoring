package com.cereal.bdgastore.data

import com.cereal.script.fixtures.FakeLogRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class ShopifyItemRepositoryIntegrationTest {
    @Tag("integration")
    @Test
    fun testGetItems() =
        runBlocking {
            val repository =
                ShopifyItemRepository(
                    FakeLogRepository(),
                    ShopifyWebsite("Test", "https://www.fillingpieces.com/collections/men-new-arrivals"),
                )

            val result = repository.getItems(null)
            assertNotNull(result)
        }
}
