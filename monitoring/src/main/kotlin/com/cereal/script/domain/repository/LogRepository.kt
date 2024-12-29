package com.cereal.script.domain.repository

interface LogRepository {
    suspend fun info(
        message: String,
        args: Map<String, Any>? = null,
    )

    suspend fun debug(
        message: String,
        args: Map<String, Any>? = null,
    )
}
