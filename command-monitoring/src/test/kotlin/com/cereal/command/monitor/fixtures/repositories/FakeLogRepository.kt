package com.cereal.command.monitor.fixtures.repositories

import com.cereal.script.repository.LogRepository

class FakeLogRepository : LogRepository {
    private val errorMessages = mutableListOf<Triple<String, Throwable?, Map<String, Any>?>>()
    private val warnMessages = mutableListOf<Pair<String, Map<String, Any>?>>()
    private val infoMessages = mutableListOf<Pair<String, Map<String, Any>?>>()
    private val debugMessages = mutableListOf<Pair<String, Map<String, Any>?>>()

    override suspend fun error(
        message: String,
        throwable: Throwable?,
        args: Map<String, Any>?,
    ) {
        errorMessages.add(Triple(message, throwable, args))
    }

    override suspend fun warn(
        message: String,
        args: Map<String, Any>?,
    ) {
        warnMessages.add(Pair(message, args))
    }

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
