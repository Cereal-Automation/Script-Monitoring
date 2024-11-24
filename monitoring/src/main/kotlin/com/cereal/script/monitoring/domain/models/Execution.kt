package com.cereal.script.monitoring.domain.models

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class Execution(
    val sequenceNumber: Int,
    val start: Instant? = null,
    val end: Instant? = null,
)

fun Execution.duration(): Duration? =
    if (start != null && end != null) {
        start.until(end, DateTimeUnit.SECOND).seconds
    } else {
        null
    }
