package com.cereal.script.commands.monitor.data.nikeold

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TestNikeApiItemRepository {
    @Tag("integration")
    @ParameterizedTest
    @MethodSource("data")
    fun testReadApi(data: TestData) =
        runBlocking {
            val repository =
                NikeApiItemRepository(
                    data.category,
                    null,
                )

            val result = repository.getItems(null)
            assert(result.items.isNotEmpty())
        }

    data class TestData(
        val category: ScrapeCategory,
    )

    companion object {
        @JvmStatic
        fun data(): List<TestData> =
            ScrapeCategory.entries
                .map { TestData(it) }
    }
}
