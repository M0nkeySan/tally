package io.github.m0nkeysan.tally.core.data.web

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Reactive data store for single values that emits Flow<T?>.
 * Automatically persists to localStorage on every update.
 * Thread-safe with Mutex for concurrent updates.
 * Used for singleton objects like UserPreferences.
 */
class WebSingleValueStore<T>(
    private val storageKey: String,
    private val storageManager: WebStorageManager,
    private val defaultValue: T? = null
) {
    private val mutex = Mutex()
    private val _flow: MutableStateFlow<T?>
    
    init {
        // Try to load from localStorage, fallback to defaultValue
        val loadedData = storageManager.load<T>(storageKey)
        _flow = MutableStateFlow(loadedData ?: defaultValue)
    }

    /**
     * Reactive flow of data value.
     */
    val flow: StateFlow<T?> = _flow.asStateFlow()

    /**
     * Update the data value with a transform function.
     * Thread-safe operation that persists to localStorage.
     */
    suspend fun update(transform: (T?) -> T?) {
        mutex.withLock {
            val newData = transform(_flow.value)
            _flow.value = newData
            if (newData != null) {
                storageManager.save(storageKey, newData)
            } else {
                storageManager.remove(storageKey)
            }
        }
    }

    /**
     * Get current data synchronously.
     */
    suspend fun get(): T? {
        return mutex.withLock {
            _flow.value
        }
    }

    /**
     * Set data directly (replaces entire value).
     */
    suspend fun set(data: T?) {
        mutex.withLock {
            _flow.value = data
            if (data != null) {
                storageManager.save(storageKey, data)
            } else {
                storageManager.remove(storageKey)
            }
        }
    }

    /**
     * Clear data (set to null).
     */
    suspend fun clear() {
        mutex.withLock {
            _flow.value = null
            storageManager.remove(storageKey)
        }
    }
}
