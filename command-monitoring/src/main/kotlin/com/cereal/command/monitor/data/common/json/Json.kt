package com.cereal.command.monitor.data.common.json

import kotlinx.serialization.json.Json

fun defaultJson() =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
