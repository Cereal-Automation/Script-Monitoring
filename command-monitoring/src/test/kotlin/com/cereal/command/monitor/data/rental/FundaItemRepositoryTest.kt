package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.fixtures.repositories.FakeLogRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class FundaItemRepositoryTest {

    @Test
    fun `parsePrice returns null for blank input`() {
        assertNull(FundaItemRepository.parsePrice(""))
    }

    @Test
    fun `parsePrice parses funda price format with slash maand`() {
        assertEquals(BigDecimal("1500"), FundaItemRepository.parsePrice("€ 1.500 /maand"))
    }

    @Test
    fun `parsePrice parses simple price`() {
        assertEquals(BigDecimal("950"), FundaItemRepository.parsePrice("€ 950"))
    }

    @Test
    fun `parseSizeM2 returns null for blank input`() {
        assertNull(FundaItemRepository.parseSizeM2(""))
    }

    @Test
    fun `parseSizeM2 parses m2 value`() {
        assertEquals(75, FundaItemRepository.parseSizeM2("75 m²"))
    }

    @Test
    fun `parseSizeM2 parses approximate m2 value with ca prefix`() {
        assertEquals(75, FundaItemRepository.parseSizeM2("ca. 75 m²"))
    }

    @Test
    fun `parseRooms returns null for blank input`() {
        assertNull(FundaItemRepository.parseRooms(""))
    }

    @Test
    fun `parseRooms parses Dutch room string`() {
        assertEquals(3, FundaItemRepository.parseRooms("3 kamers"))
    }

    @Test
    fun `parseRooms parses bare integer`() {
        assertEquals(2, FundaItemRepository.parseRooms("2"))
    }

    @Test
    fun `passesFilters returns true when all filters null`() {
        val repo = FundaItemRepository(
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
        val repo = FundaItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1200,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = BigDecimal("1500"), sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters includes listing when price is null and maxPrice is set (conservative)`() {
        val repo = FundaItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = 1200,
            minSizeM2 = null,
            minRooms = null,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns true when sizeM2 is null and minSizeM2 is set (conservative)`() {
        val repo = FundaItemRepository(
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
        val repo = FundaItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = 3,
            logRepository = FakeLogRepository(),
        )
        assert(repo.passesFilters(price = null, sizeM2 = null, rooms = null))
    }

    @Test
    fun `passesFilters returns false when size is below minSizeM2`() {
        val repo = FundaItemRepository(
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
        val repo = FundaItemRepository(
            cities = listOf("amsterdam"),
            maxPrice = null,
            minSizeM2 = null,
            minRooms = 3,
            logRepository = FakeLogRepository(),
        )
        assert(!repo.passesFilters(price = null, sizeM2 = null, rooms = 2))
    }
}
