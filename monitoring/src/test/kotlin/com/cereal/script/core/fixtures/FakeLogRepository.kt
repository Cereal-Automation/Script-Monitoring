package com.cereal.script.core.fixtures

import com.cereal.script.core.domain.repository.LogRepository

class FakeLogRepository : LogRepository {
    private val infoMessages = mutableListOf<Pair<String, Map<String, Any>?>>()
    private val debugMessages = mutableListOf<Pair<String, Map<String, Any>?>>()

    override suspend fun info(
        message: String,
        args: Map<String, Any>?,
    ) {
        infoMessages.add(Pair(message, args))
    }

    override suspend fun debug(
        message: String,
        args: Map<String, Any>?,
    ) {
        debugMessages.add(Pair(message, args))
    }
}
