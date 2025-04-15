package com.cereal.command.monitor.data.zalando

import com.cereal.command.monitor.fixtures.repositories.FakeLogRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.junit5.JUnit5Asserter.assertNotNull
import kotlin.test.junit5.JUnit5Asserter.fail

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
                    data.type,
                )

            try {
                val result = repository.getItems(null)
                assert(result.items.isNotEmpty()) { "Expected non-empty items list" }

                result.items.forEach { item ->
                    assertNotNull(item.id) { "Item ID should not be null" }
                    assertNotNull(item.name) { "Item name should not be null" }
                }
            } catch (e: Exception) {
                fail("Repository threw unexpected exception: ${e.message}", e)
            }
        }

    data class TestData(
        val category: ZalandoProductCategory,
        val website: ZalandoWebsite,
        val type: ZalandoMonitorType,
    )

    companion object {
        @JvmStatic
        fun data(): List<TestData> =
            ZalandoWebsite.entries
                .map { website ->
                    ZalandoProductCategory.entries
                        .map { category ->
                            ZalandoMonitorType.entries.map { monitorType ->
                                TestData(category, website, monitorType)
                            }
                        }.flatten()
                }.flatten()
    }
}
