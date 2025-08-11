package com.cereal.command.monitor.data.common.cache

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

interface CacheManager {
    /**
     * Stores a value with a key and expiration time
     */
    suspend fun store(key: String, value: String, expiration: Duration = 24.hours)
    
    /**
     * Retrieves a cached value if it hasn't expired, null otherwise
     */
    suspend fun retrieve(key: String): String?
    
    /**
     * Checks if a cached value exists and hasn't expired
     */
    suspend fun isValid(key: String): Boolean
}