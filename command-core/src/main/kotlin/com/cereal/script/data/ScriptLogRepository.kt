package com.cereal.script.data

import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.logger.LoggerComponent

class ScriptLogRepository(
    private val loggerComponent: LoggerComponent,
    private val statusUpdate:
        suspend (message: String) -> Unit,
) : LogRepository {
    override suspend fun info(
        message: String,
        args: Map<String, Any>?,
    ) {
        addLog(message, args, true)
    }

    override suspend fun debug(
        message: String,
        args: Map<String, Any>?,
    ) {
        addLog(message, args, false)
    }

    private suspend fun addLog(
        message: String,
        args: Map<String, Any>?,
        includeStatusUpdate: Boolean = false,
    ) {
        val formattedMessage =
            if (args.isNullOrEmpty()) {
                message
            } else {
                "$message [${args.entries.joinToString(", ") { "${it.key}=${it.value}" }}]"
            }

        if (includeStatusUpdate) {
            statusUpdate(formattedMessage)
        }

        loggerComponent.info(formattedMessage)
    }
}
