package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.sdk.component.logger.LoggerComponent

class ScriptLogRepository(private val loggerComponent: LoggerComponent, private val statusUpdate: suspend (message: String) -> Unit): LogRepository {

    override suspend fun add(message: String) {
        statusUpdate(message)
        loggerComponent.info(message)
    }
}
