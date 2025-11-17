package com.cereal.command.monitor.data.bolcom

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlin.collections.iterator

private val jsonLoose =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

private fun JsonElement.isAllTokenKeyedObject(): Boolean {
    val obj = this as? JsonObject ?: return false
    if (obj.isEmpty()) return false
    return obj.keys.all { it.startsWith("_") && it.drop(1).all(Char::isDigit) }
}

private fun JsonElement.asIntOrNull(): Int? = (this as? JsonPrimitive)?.intOrNull

private fun buildResolver(table: JsonArray): (JsonElement, MutableSet<Int>) -> JsonElement {
    lateinit var resolve: (JsonElement, MutableSet<Int>) -> JsonElement

    fun resolveIndex(
        index: Int,
        seen: MutableSet<Int>,
    ): JsonElement {
        if (index !in table.indices) return JsonPrimitive(index)
        if (!seen.add(index)) return JsonNull
        val el = table[index]
        val out = resolve(el, seen)
        seen.remove(index)
        return out
    }

    resolve = { el: JsonElement, seen: MutableSet<Int> ->
        when (el) {
            is JsonPrimitive -> {
                val n = el.intOrNull
                if (n != null && n >= 0 && n < table.size) resolveIndex(n, seen) else el
            }
            is JsonArray -> JsonArray(el.map { child -> resolve(child, seen) })
            is JsonObject -> {
                if (el.isAllTokenKeyedObject()) {
                    val mapped =
                        buildMap<String, JsonElement> {
                            for ((k, v) in el) {
                                val keyIndex = k.drop(1).toIntOrNull() ?: continue
                                val keyEl = resolveIndex(keyIndex, seen)
                                val key = (keyEl as? JsonPrimitive)?.content ?: continue
                                val value = v.asIntOrNull()?.let { vi -> resolveIndex(vi, seen) } ?: resolve(v, seen)
                                put(key, value)
                            }
                        }
                    JsonObject(mapped)
                } else {
                    JsonObject(el.mapValues { (_, v) -> resolve(v, seen) })
                }
            }
        }
    }

    return resolve
}

fun tryDecodeInternedTable(raw: String): JsonElement? {
    val parsed = runCatching { jsonLoose.parseToJsonElement(raw) }.getOrNull() ?: return null
    val arr = parsed as? JsonArray ?: return null
    if (arr.isEmpty()) return null

    val hasTokenObj = arr.any { it.isAllTokenKeyedObject() }
    if (!hasTokenObj) return null

    val resolve = buildResolver(arr)

    val rootCandidate: JsonElement =
        arr
            .windowed(2, 1)
            .firstOrNull { w ->
                val first = w[0] as? JsonPrimitive
                first?.isString == true && first.content == "data"
            }?.get(1) ?: arr.last()

    return resolve(rootCandidate, mutableSetOf())
}
