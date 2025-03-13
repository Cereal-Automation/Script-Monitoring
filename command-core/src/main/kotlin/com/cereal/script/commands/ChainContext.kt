package com.cereal.script.commands

import kotlin.reflect.KClass

class ChainContext {
    val store: MutableList<Any> = mutableListOf() // Mutable store

    fun <T : Any> get(kClass: KClass<T>): T? = store.filterIsInstance(kClass.java).firstOrNull()

    inline fun <reified T : Any> get(): T? = store.filterIsInstance<T>().firstOrNull()

    fun put(obj: Any) {
        store.removeIf { it::class == obj::class } // Remove existing object of the same type
        store.add(obj) // Add new object
    }

    inline fun <reified T : Any> getOrCreate(create: () -> T): T {
        // Try to get the existing instance, or create a new one if it doesn't exist
        return get<T>() ?: create().also { put(it) }
    }

    inline fun <reified T : Any> getOrThrow(): T {
        // Try to get the object from the store, if not found throw an exception
        return get<T>() ?: throw NoSuchElementException("No instance of type ${T::class} found in store.")
    }
}
