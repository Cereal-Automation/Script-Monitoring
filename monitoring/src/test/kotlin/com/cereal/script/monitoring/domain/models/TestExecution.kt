package com.cereal.script.monitoring.domain.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class TestExecution {
    @Test
    fun `test duration with both start and end`() {
        val start = Instant.parse("2023-01-01T12:00:00Z")
        val end = Instant.parse("2023-01-01T12:01:00Z")
        val execution = Execution(sequenceNumber = 1, start = start, end = end)

        val duration = execution.duration()

        assertEquals(60.seconds, duration)
    }

    @Test
    fun `test duration with null start`() {
        val end = Instant.parse("2023-01-01T12:01:00Z")
        val execution = Execution(sequenceNumber = 1, start = null, end = end)

        val duration = execution.duration()

        assertNull(duration)
    }

    @Test
    fun `test duration with null end`() {
        val start = Instant.parse("2023-01-01T12:00:00Z")
        val execution = Execution(sequenceNumber = 1, start = start, end = null)

        val duration = execution.duration()

        assertNull(duration)
    }

    @Test
    fun `test duration with both start and end null`() {
        val execution = Execution(sequenceNumber = 1, start = null, end = null)

        val duration = execution.duration()

        assertNull(duration)
    }
}
