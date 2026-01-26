package io.github.m0nkeysan.tally.core.data.web

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/**
 * Wrapper around browser's localStorage API with JSON serialization.
 * Provides type-safe storage operations for web platform.
 */
class WebStorageManager {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    /**
     * Save data to localStorage with JSON serialization.
     * @return true if successful, false if quota exceeded or other error
     */
    inline fun <reified T> save(key: String, data: T): Boolean {
        return try {
            val jsonString = json.encodeToString(data)
            js("localStorage.setItem(key, jsonString)")
            true
        } catch (e: SerializationException) {
            console.error("Failed to serialize data for key '$key': ${e.message}")
            false
        } catch (e: dynamic) {
            // Handle QuotaExceededError
            if (e.name == "QuotaExceededError") {
                console.warn("localStorage quota exceeded for key '$key'")
            } else {
                console.error("Failed to save to localStorage for key '$key': ${e.message}")
            }
            false
        }
    }

    /**
     * Load data from localStorage with JSON deserialization.
     * @return deserialized data or null if not found or error
     */
    inline fun <reified T> load(key: String): T? {
        return try {
            val jsonString = js("localStorage.getItem(key)")
            if (jsonString == null || jsonString == js("null")) {
                null
            } else {
                json.decodeFromString<T>(jsonString as String)
            }
        } catch (e: SerializationException) {
            console.error("Failed to deserialize data for key '$key': ${e.message}")
            null
        } catch (e: Exception) {
            console.error("Failed to load from localStorage for key '$key': ${e.message}")
            null
        }
    }

    /**
     * Remove an item from localStorage.
     */
    fun remove(key: String) {
        try {
            js("localStorage.removeItem(key)")
        } catch (e: Exception) {
            console.error("Failed to remove key '$key' from localStorage: ${e.message}")
        }
    }

    /**
     * Clear all items from localStorage.
     */
    fun clear() {
        try {
            js("localStorage.clear()")
        } catch (e: Exception) {
            console.error("Failed to clear localStorage: ${e.message}")
        }
    }

    /**
     * Get all keys from localStorage.
     */
    fun getAllKeys(): List<String> {
        return try {
            val length = js("localStorage.length") as Int
            val keys = mutableListOf<String>()
            for (i in 0 until length) {
                val key = js("localStorage.key(i)") as? String
                if (key != null) {
                    keys.add(key)
                }
            }
            keys
        } catch (e: Exception) {
            console.error("Failed to get keys from localStorage: ${e.message}")
            emptyList()
        }
    }

    /**
     * Check if a key exists in localStorage.
     */
    fun hasKey(key: String): Boolean {
        return try {
            val value = js("localStorage.getItem(key)")
            value != null && value != js("null")
        } catch (e: Exception) {
            false
        }
    }
}
