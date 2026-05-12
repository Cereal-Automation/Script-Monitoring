package com.cereal.command.monitor.data.rental

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
}
