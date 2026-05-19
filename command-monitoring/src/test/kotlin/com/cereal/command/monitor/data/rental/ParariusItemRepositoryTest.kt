package com.cereal.command.monitor.data.rental

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ParariusItemRepositoryTest {
    @Test
    fun `parsePrice returns null for blank input`() {
        assertNull(BrowserBasedItemRepository.parsePrice(""))
    }

    @Test
    fun `parsePrice parses euro price with dots and spaces`() {
        assertEquals(BigDecimal("1250"), BrowserBasedItemRepository.parsePrice("€ 1.250 per month"))
    }

    @Test
    fun `parsePrice parses simple euro price`() {
        assertEquals(BigDecimal("950"), BrowserBasedItemRepository.parsePrice("€950 per month"))
    }

    @Test
    fun `parsePrice parses pcm format`() {
        assertEquals(BigDecimal("2750"), BrowserBasedItemRepository.parsePrice("€2,750 pcm"))
    }

    @Test
    fun `parseSizeM2 returns null for blank input`() {
        assertNull(BrowserBasedItemRepository.parseSizeM2(""))
    }

    @Test
    fun `parseSizeM2 parses m2 value`() {
        assertEquals(85, BrowserBasedItemRepository.parseSizeM2("85 m²"))
    }

    @Test
    fun `parseSizeM2 parses approximate m2 value with ca prefix`() {
        assertEquals(85, BrowserBasedItemRepository.parseSizeM2("ca. 85 m²"))
    }

    @Test
    fun `parseRooms returns null for blank input`() {
        assertNull(BrowserBasedItemRepository.parseRooms(""))
    }

    @Test
    fun `parseRooms parses room count`() {
        assertEquals(3, BrowserBasedItemRepository.parseRooms("3 rooms"))
    }

    @Test
    fun `parseRooms parses bare integer`() {
        assertEquals(2, BrowserBasedItemRepository.parseRooms("2"))
    }
}
