package com.cereal.script.monitoring.domain.repository

interface LogRepository {
    suspend fun add(
        message: String,
        args: Map<String, Any>? = null,
    )
}
