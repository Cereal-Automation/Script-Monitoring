package com.cereal.command.monitor.models

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.math.BigDecimal
import kotlin.test.Test

class ItemFilterTest {
    private fun itemWithPrice(amount: String) =
        Item(
            id = "1",
            url = null,
            name = "Test",
            properties = listOf(ItemProperty.Price(BigDecimal(amount), Currency.EUR)),
        )

    private fun itemWithCustom(
        name: String,
        value: String,
    ) = Item(
        id = "1",
        url = null,
        name = "Test",
        properties = listOf(ItemProperty.Custom(name, value)),
    )

    private fun emptyItem() = Item(id = "1", url = null, name = "Test")

    // PriceAtMost

    @Test
    fun `PriceAtMost passes when price is below threshold`() {
        val item = itemWithPrice("1500")
        assertTrue(item.passes(listOf(ItemFilter.PriceAtMost(BigDecimal("2000")))))
    }

    @Test
    fun `PriceAtMost passes when price equals threshold`() {
        val item = itemWithPrice("2000")
        assertTrue(item.passes(listOf(ItemFilter.PriceAtMost(BigDecimal("2000")))))
    }

    @Test
    fun `PriceAtMost excludes when price exceeds threshold`() {
        val item = itemWithPrice("2500")
        assertFalse(item.passes(listOf(ItemFilter.PriceAtMost(BigDecimal("2000")))))
    }

    @Test
    fun `PriceAtMost excludes when price property is missing`() {
        assertFalse(emptyItem().passes(listOf(ItemFilter.PriceAtMost(BigDecimal("2000")))))
    }

    // PriceAtLeast

    @Test
    fun `PriceAtLeast passes when price is above threshold`() {
        val item = itemWithPrice("1500")
        assertTrue(item.passes(listOf(ItemFilter.PriceAtLeast(BigDecimal("1000")))))
    }

    @Test
    fun `PriceAtLeast excludes when price is below threshold`() {
        val item = itemWithPrice("500")
        assertFalse(item.passes(listOf(ItemFilter.PriceAtLeast(BigDecimal("1000")))))
    }

    @Test
    fun `PriceAtLeast excludes when price property is missing`() {
        assertFalse(emptyItem().passes(listOf(ItemFilter.PriceAtLeast(BigDecimal("1000")))))
    }

    // CustomValueAtLeast — plain number

    @Test
    fun `CustomValueAtLeast passes when value meets threshold`() {
        val item = itemWithCustom("Rooms", "3")
        assertTrue(item.passes(listOf(ItemFilter.CustomValueAtLeast("Rooms", 2.0))))
    }

    @Test
    fun `CustomValueAtLeast passes when value has unit suffix`() {
        val item = itemWithCustom("Size", "85 m²")
        assertTrue(item.passes(listOf(ItemFilter.CustomValueAtLeast("Size", 50.0))))
    }

    @Test
    fun `CustomValueAtLeast excludes when value is below threshold`() {
        val item = itemWithCustom("Rooms", "1")
        assertFalse(item.passes(listOf(ItemFilter.CustomValueAtLeast("Rooms", 2.0))))
    }

    @Test
    fun `CustomValueAtLeast excludes when property is missing`() {
        assertFalse(emptyItem().passes(listOf(ItemFilter.CustomValueAtLeast("Rooms", 2.0))))
    }

    @Test
    fun `CustomValueAtLeast excludes when value is non-numeric`() {
        val item = itemWithCustom("Rooms", "unknown")
        assertFalse(item.passes(listOf(ItemFilter.CustomValueAtLeast("Rooms", 2.0))))
    }

    // CustomValueAtMost

    @Test
    fun `CustomValueAtMost passes when value is below threshold`() {
        val item = itemWithCustom("Size", "60 m²")
        assertTrue(item.passes(listOf(ItemFilter.CustomValueAtMost("Size", 80.0))))
    }

    @Test
    fun `CustomValueAtMost excludes when value exceeds threshold`() {
        val item = itemWithCustom("Size", "100 m²")
        assertFalse(item.passes(listOf(ItemFilter.CustomValueAtMost("Size", 80.0))))
    }

    @Test
    fun `CustomValueAtMost excludes when property is missing`() {
        assertFalse(emptyItem().passes(listOf(ItemFilter.CustomValueAtMost("Size", 80.0))))
    }

    // CustomValueEquals

    @Test
    fun `CustomValueEquals passes on exact match`() {
        val item = itemWithCustom("Source", "Funda")
        assertTrue(item.passes(listOf(ItemFilter.CustomValueEquals("Source", "Funda"))))
    }

    @Test
    fun `CustomValueEquals passes case-insensitively`() {
        val item = itemWithCustom("Source", "funda")
        assertTrue(item.passes(listOf(ItemFilter.CustomValueEquals("Source", "Funda"))))
    }

    @Test
    fun `CustomValueEquals excludes on mismatch`() {
        val item = itemWithCustom("Source", "Pararius")
        assertFalse(item.passes(listOf(ItemFilter.CustomValueEquals("Source", "Funda"))))
    }

    @Test
    fun `CustomValueEquals excludes when property is missing`() {
        assertFalse(emptyItem().passes(listOf(ItemFilter.CustomValueEquals("Source", "Funda"))))
    }

    // AND semantics

    @Test
    fun `passes returns true when filter list is empty`() {
        assertTrue(emptyItem().passes(emptyList()))
    }

    @Test
    fun `passes returns false when any filter fails`() {
        val item =
            Item(
                id = "1",
                url = null,
                name = "Test",
                properties =
                    listOf(
                        ItemProperty.Price(BigDecimal("1500"), Currency.EUR),
                        ItemProperty.Custom("Rooms", "1"),
                    ),
            )
        val filters =
            listOf(
                // passes
                ItemFilter.PriceAtMost(BigDecimal("2000")),
                // fails
                ItemFilter.CustomValueAtLeast("Rooms", 2.0),
            )
        assertFalse(item.passes(filters))
    }

    @Test
    fun `passes returns true when all filters pass`() {
        val item =
            Item(
                id = "1",
                url = null,
                name = "Test",
                properties =
                    listOf(
                        ItemProperty.Price(BigDecimal("1500"), Currency.EUR),
                        ItemProperty.Custom("Rooms", "3"),
                    ),
            )
        val filters =
            listOf(
                ItemFilter.PriceAtMost(BigDecimal("2000")),
                ItemFilter.CustomValueAtLeast("Rooms", 2.0),
            )
        assertTrue(item.passes(filters))
    }
}
