package com.cereal.script.data.json

import kotlinx.serialization.json.Json

fun defaultJson() =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
