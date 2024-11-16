package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.data.item.nike.NikeApiItemRepository
import com.cereal.script.monitoring.data.item.nike.ScrapeCategory
import com.cereal.script.monitoring.domain.models.Item
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TestNikeApiItemRepository {
    @Tag("integration")
    @ParameterizedTest
    @MethodSource("data")
    fun testSuccess(data: TestData) =
        runBlocking {
            val repository = NikeApiItemRepository(data.category, null)

            val collectedItems = mutableListOf<Item>()
            repository.getItems().collect { collectedItems.add(it) }
        }

    data class TestData(
        val category: ScrapeCategory,
    )

    companion object {
        @JvmStatic
        fun data(): List<TestData> = ScrapeCategory.entries.map { TestData(it) }
    }
}
