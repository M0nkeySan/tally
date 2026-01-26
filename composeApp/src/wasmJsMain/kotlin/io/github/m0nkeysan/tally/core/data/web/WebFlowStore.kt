package io.github.m0nkeysan.tally.core.data.web

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Reactive data store for lists that emits Flow<List<T>>.
 * Automatically persists to localStorage on every update.
 * Thread-safe with Mutex for concurrent updates.
 */
class WebFlowStore<T>(
    private val storageKey: String,
    private val storageManager: WebStorageManager,
    initialData: List<T> = emptyList()
) {
    private val mutex = Mutex()
    private val _flow: MutableStateFlow<List<T>>
    
    init {
        // Try to load from localStorage, fallback to initialData
        val loadedData = storageManager.load<List<T>>(storageKey)
        _flow = MutableStateFlow(loadedData ?: initialData)
    }

    /**
     * Reactive flow of data list.
     */
    val flow: StateFlow<List<T>> = _flow.asStateFlow()

    /**
     * Update the data list with a transform function.
     * Thread-safe operation that persists to localStorage.
     */
    suspend fun update(transform: (List<T>) -> List<T>) {
        mutex.withLock {
            val newData = transform(_flow.value)
            _flow.value = newData
            storageManager.save(storageKey, newData)
        }
    }

    /**
     * Get current data synchronously.
     */
    suspend fun get(): List<T> {
        return mutex.withLock {
            _flow.value
        }
    }

    /**
     * Set data directly (replaces entire list).
     */
    suspend fun set(data: List<T>) {
        mutex.withLock {
            _flow.value = data
            storageManager.save(storageKey, data)
        }
    }

    /**
     * Clear all data (set to empty list).
     */
    suspend fun clear() {
        mutex.withLock {
            _flow.value = emptyList()
            storageManager.remove(storageKey)
        }
    }
}
