package com.cereal.snkrs.data

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class SnkrsItemRepositoryIntegrationTest {
    @Tag("integration")
    @Test
    fun testGetItems() =
        runBlocking {
            val repository =
                SnkrsItemRepository(
                    SnkrsApiClient(null),
                    Locale.BE_NL,
                )

            val result = repository.getItems(null)
            assertNotNull(result)
        }
}
