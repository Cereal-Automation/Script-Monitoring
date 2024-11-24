package com.cereal.script.monitoring.fixtures

import com.cereal.script.monitoring.domain.repository.LogRepository

class FakeLogRepository : LogRepository {
    val messages = mutableListOf<Pair<String, Map<String, Any>?>>()

    override suspend fun add(
        message: String,
        args: Map<String, Any>?,
    ) {
        messages.add(Pair(message, args))
    }
}
