package com.cereal.command.monitor.data.tgtg.apiclients

import com.cereal.command.monitor.data.tgtg.TgtgConfig
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import kotlinx.serialization.json.Json

/**
 * Simple persistence wrapper around [PreferenceComponent] for reading/writing [TgtgConfig].
 * Centralises JSON (de)serialisation and error handling.
 */
internal class TgtgConfigStore(
    private val preferenceComponent: PreferenceComponent,
    private val json: Json,
    private val logRepository: LogRepository,
    private val key: String = "tgtg_config",
) {
    suspend fun get(): TgtgConfig {
        val configJson = preferenceComponent.getString(key)
        if (configJson.isNullOrEmpty()) return TgtgConfig()
        return try {
            json.decodeFromString(TgtgConfig.serializer(), configJson)
        } catch (e: Exception) {
            logRepository.debug("Failed to deserialize stored config: ${e.message}")
            TgtgConfig()
        }
    }

    suspend fun set(config: TgtgConfig) {
        val serialized = json.encodeToString(TgtgConfig.serializer(), config)
        preferenceComponent.setString(key, serialized)
    }

    /** Update using a transform function (read-modify-write) */
    suspend fun update(transform: (TgtgConfig) -> TgtgConfig): TgtgConfig {
        val current = get()
        val updated = transform(current)
        set(updated)
        return updated
    }
}
