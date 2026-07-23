package com.cereal.scraping.json

import kotlinx.serialization.json.Json

fun defaultJson() =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
