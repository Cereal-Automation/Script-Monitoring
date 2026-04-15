package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.fixtures.repositories.FakeLogRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertTrue

class TestFundaItemRepository {
    @Tag("integration")
    @ParameterizedTest
    @MethodSource("data")
    fun testGetItems(data: TestData) =
        runBlocking {
            val repository =
                FundaItemRepository(
                    cities = listOf(data.city),
                    maxPrice = 6000,
                    minSizeM2 = 10,
                    minRooms = 1,
                    furnishing = Furnishing.FURNISHED,
                    propertyType = PropertyType.APARTMENT,
                    logRepository = FakeLogRepository(),
                )

            val result = repository.getItems(null)
            assertTrue(result.items.isNotEmpty(), "Expected listings for city '${data.city}' but got none")
        }

    data class TestData(
        val city: String,
    )

    companion object {
        @JvmStatic
        fun data(): List<TestData> =
            listOf(
                TestData("amsterdam"),
                TestData("rotterdam"),
                TestData("utrecht"),
            )
    }
}
