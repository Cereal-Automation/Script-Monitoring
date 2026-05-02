package com.cereal.script.repository

interface LogRepository {
    suspend fun error(
        message: String,
        throwable: Throwable? = null,
        args: Map<String, Any>? = null,
    )

    suspend fun warn(
        message: String,
        args: Map<String, Any>? = null,
    )

    suspend fun info(
        message: String,
        args: Map<String, Any>? = null,
    )

    suspend fun debug(
        message: String,
        args: Map<String, Any>? = null,
    )
}
