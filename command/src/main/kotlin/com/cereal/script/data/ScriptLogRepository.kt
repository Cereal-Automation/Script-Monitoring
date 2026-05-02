package com.cereal.script.data

import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.logger.LoggerComponent

class ScriptLogRepository(
    private val loggerComponent: LoggerComponent,
    private val statusUpdate:
        suspend (message: String) -> Unit,
) : LogRepository {
    override suspend fun error(
        message: String,
        throwable: Throwable?,
        args: Map<String, Any>?,
    ) {
        val formattedMessage = formatMessage(message, args)
        statusUpdate(formattedMessage)
        loggerComponent.error(formattedMessage, throwable)
    }

    override suspend fun warn(
        message: String,
        args: Map<String, Any>?,
    ) {
        val formattedMessage = formatMessage(message, args)
        statusUpdate(formattedMessage)
        loggerComponent.warn(formattedMessage)
    }

    override suspend fun info(
        message: String,
        args: Map<String, Any>?,
    ) {
        val formattedMessage = formatMessage(message, args)
        statusUpdate(formattedMessage)
        loggerComponent.info(formattedMessage)
    }

    override suspend fun debug(
        message: String,
        args: Map<String, Any>?,
    ) {
        val formattedMessage = formatMessage(message, args)
        loggerComponent.debug(formattedMessage)
    }

    private fun formatMessage(
        message: String,
        args: Map<String, Any>?,
    ): String =
        if (args.isNullOrEmpty()) {
            message
        } else {
            "$message [${args.entries.joinToString(", ") { "${it.key}=${it.value}" }}]"
        }
}
