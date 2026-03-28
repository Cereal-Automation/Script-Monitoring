package com.cereal.command.monitor.data.rental

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ParariusItemRepositoryTest {

    @Test
    fun `parsePrice returns null for blank input`() {
        assertNull(ParariusItemRepository.parsePrice(""))
    }

    @Test
    fun `parsePrice parses euro price with dots and spaces`() {
        assertEquals(BigDecimal("1250"), ParariusItemRepository.parsePrice("€ 1.250 per month"))
    }

    @Test
    fun `parsePrice parses simple euro price`() {
        assertEquals(BigDecimal("950"), ParariusItemRepository.parsePrice("€950 per month"))
    }

    @Test
    fun `parseSizeM2 returns null for blank input`() {
        assertNull(ParariusItemRepository.parseSizeM2(""))
    }

    @Test
    fun `parseSizeM2 parses m2 value`() {
        assertEquals(85, ParariusItemRepository.parseSizeM2("85 m²"))
    }

    @Test
    fun `parseRooms returns null for blank input`() {
        assertNull(ParariusItemRepository.parseRooms(""))
    }

    @Test
    fun `parseRooms parses room count`() {
        assertEquals(3, ParariusItemRepository.parseRooms("3 rooms"))
    }

    @Test
    fun `parseRooms parses bare integer`() {
        assertEquals(2, ParariusItemRepository.parseRooms("2"))
    }

    @Test
    fun `passesFilters returns true when all filters null`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns false when price exceeds maxPrice`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1000,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = BigDecimal("1200"), sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns true when price is null and maxPrice is set (conservative)`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1000,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns false when size is below minSizeM2`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = 60,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = null, sizeM2 = 45, rooms = null))
    }

    @Test
    fun `passesFilters returns false when rooms below minRooms`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = 3,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = null, sizeM2 = null, rooms = 2))
    }

    @Test
    fun `passesFilters returns true when sizeM2 is null and minSizeM2 is set (conservative)`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = 60,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns true when rooms is null and minRooms is set (conservative)`() {
        val repo = ParariusItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = 3,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }
}
