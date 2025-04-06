package com.cereal.command.monitor.data.zalando

import com.cereal.command.monitor.fixtures.repositories.FakeLogRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TestZalandoItemRepository {
    @Tag("integration")
    @ParameterizedTest
    @MethodSource("data")
    fun testReadApi(data: TestData) =
        runBlocking {
            val repository =
                ZalandoItemRepository(
                    FakeLogRepository(),
                    data.category,
                    data.website,
                )

            val result = repository.getItems(null)
            assert(result.items.isNotEmpty())
        }

    data class TestData(
        val category: ZalandoProductCategory,
        val website: ZalandoWebsite,
    )

    companion object {
        @JvmStatic
        fun data(): List<TestData> =
            ZalandoWebsite.entries
                .map { website ->
                    ZalandoProductCategory.entries
                        .map { category -> TestData(category, website) }
                }.flatten()
    }
}
