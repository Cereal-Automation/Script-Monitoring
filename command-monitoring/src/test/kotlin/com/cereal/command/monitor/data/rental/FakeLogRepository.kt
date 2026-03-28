package com.cereal.command.monitor.data.rental

import com.cereal.script.repository.LogRepository

class FakeLogRepository : LogRepository {
    override suspend fun debug(message: String, args: Map<String, Any>?) {}
    override suspend fun info(message: String, args: Map<String, Any>?) {}
}
