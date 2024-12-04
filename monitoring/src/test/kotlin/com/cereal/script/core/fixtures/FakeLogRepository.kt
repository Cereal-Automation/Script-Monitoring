package com.cereal.script.core.fixtures

import com.cereal.script.core.domain.repository.LogRepository

class FakeLogRepository : LogRepository {
    val messages = mutableListOf<Pair<String, Map<String, Any>?>>()

    override suspend fun add(
        message: String,
        args: Map<String, Any>?,
    ) {
        messages.add(Pair(message, args))
    }
}
