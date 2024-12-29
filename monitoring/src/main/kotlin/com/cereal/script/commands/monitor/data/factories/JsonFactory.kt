package com.cereal.script.commands.monitor.data.factories

import kotlinx.serialization.json.Json

object JsonFactory {
    fun create() =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
}
