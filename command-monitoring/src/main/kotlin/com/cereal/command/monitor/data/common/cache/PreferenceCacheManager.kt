package com.cereal.command.monitor.data.common.cache

import com.cereal.sdk.component.preference.PreferenceComponent
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PreferenceCacheManager(
    private val preferenceComponent: PreferenceComponent,
) : CacheManager {
    override suspend fun store(
        key: String,
        value: String,
        expiration: Duration,
    ) {
        val expirationTime = Clock.System.now().plus(expiration)
        val cacheData = CacheData(value, expirationTime.epochSeconds)

        preferenceComponent.setString("${CACHE_PREFIX}${key}_value", cacheData.value)
        preferenceComponent.setString("${CACHE_PREFIX}${key}_expiration", cacheData.expirationEpoch.toString())
    }

    override suspend fun retrieve(key: String): String? {
        if (!isValid(key)) {
            return null
        }

        return preferenceComponent.getString("${CACHE_PREFIX}${key}_value")
    }

    override suspend fun isValid(key: String): Boolean {
        val expirationString =
            preferenceComponent.getString("${CACHE_PREFIX}${key}_expiration")
                ?: return false

        val expirationEpoch = expirationString.toLongOrNull() ?: return false
        val expirationTime = Instant.fromEpochSeconds(expirationEpoch)

        return Clock.System.now() < expirationTime
    }

    private data class CacheData(
        val value: String,
        val expirationEpoch: Long,
    )

    companion object {
        private const val CACHE_PREFIX = "cache_"
    }
}
