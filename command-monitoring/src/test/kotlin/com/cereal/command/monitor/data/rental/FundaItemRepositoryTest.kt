package com.cereal.command.monitor.data.rental

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class FundaItemRepositoryTest {
    @Test
    fun `parsePrice returns null for blank input`() {
        assertNull(BrowserBasedItemRepository.parsePrice(""))
    }

    @Test
    fun `parsePrice parses funda price format with slash maand`() {
        assertEquals(BigDecimal("1500"), BrowserBasedItemRepository.parsePrice("€ 1.500 /maand"))
    }

    @Test
    fun `parsePrice parses simple price`() {
        assertEquals(BigDecimal("950"), BrowserBasedItemRepository.parsePrice("€ 950"))
    }

    @Test
    fun `parseSizeM2 returns null for blank input`() {
        assertNull(BrowserBasedItemRepository.parseSizeM2(""))
    }

    @Test
    fun `parseSizeM2 parses m2 value`() {
        assertEquals(75, BrowserBasedItemRepository.parseSizeM2("75 m²"))
    }

    @Test
    fun `parseSizeM2 parses approximate m2 value with ca prefix`() {
        assertEquals(75, BrowserBasedItemRepository.parseSizeM2("ca. 75 m²"))
    }

    @Test
    fun `parseRooms returns null for blank input`() {
        assertNull(BrowserBasedItemRepository.parseRooms(""))
    }

    @Test
    fun `parseRooms parses Dutch room string`() {
        assertEquals(3, BrowserBasedItemRepository.parseRooms("3 kamers"))
    }

    @Test
    fun `parseRooms parses bare integer`() {
        assertEquals(2, BrowserBasedItemRepository.parseRooms("2"))
    }
}
