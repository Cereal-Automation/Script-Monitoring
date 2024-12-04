package com.cereal.script.core.domain.repository

interface LogRepository {
    suspend fun add(
        message: String,
        args: Map<String, Any>? = null,
    )
}
