package com.cereal.command.monitor.data.tgtg.apiclients

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtTest {
    @Test
    fun `compareVersions should return 0 for identical versions`() {
        // Given
        val version1 = "1.2.3"
        val version2 = "1.2.3"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `compareVersions should return negative when first version is less than second`() {
        // Given
        val version1 = "1.2.3"
        val version2 = "1.2.4"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should return positive when first version is greater than second`() {
        // Given
        val version1 = "1.2.4"
        val version2 = "1.2.3"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun `compareVersions should handle different number of version parts`() {
        // Given
        val version1 = "1.2"
        val version2 = "1.2.0"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `compareVersions should handle different number of version parts with difference`() {
        // Given
        val version1 = "1.2"
        val version2 = "1.2.1"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle major version differences`() {
        // Given
        val version1 = "1.0.0"
        val version2 = "2.0.0"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle minor version differences`() {
        // Given
        val version1 = "1.1.0"
        val version2 = "1.2.0"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle patch version differences`() {
        // Given
        val version1 = "1.2.1"
        val version2 = "1.2.2"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle single digit versions`() {
        // Given
        val version1 = "1"
        val version2 = "2"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle empty strings`() {
        // Given
        val version1 = ""
        val version2 = ""

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `compareVersions should handle empty string vs non-empty`() {
        // Given
        val version1 = ""
        val version2 = "1.0.0"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle non-empty vs empty string`() {
        // Given
        val version1 = "1.0.0"
        val version2 = ""

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun `compareVersions should handle versions with leading zeros`() {
        // Given
        val version1 = "01.02.03"
        val version2 = "1.2.3"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `compareVersions should handle complex version comparisons`() {
        // Given
        val version1 = "10.20.30"
        val version2 = "10.20.29"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun `compareVersions should handle very different version lengths`() {
        // Given
        val version1 = "1"
        val version2 = "1.2.3.4.5"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle reverse order of very different version lengths`() {
        // Given
        val version1 = "1.2.3.4.5"
        val version2 = "1"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun `compareVersions should handle edge case with zero versions`() {
        // Given
        val version1 = "0.0.0"
        val version2 = "0.0.1"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `compareVersions should handle large version numbers`() {
        // Given
        val version1 = "999.999.999"
        val version2 = "1000.0.0"

        // When
        val result = version1.compareVersions(version2)

        // Then
        assertEquals(-1, result)
    }
}
