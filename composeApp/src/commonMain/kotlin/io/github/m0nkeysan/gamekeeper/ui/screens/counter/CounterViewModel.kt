package io.github.m0nkeysan.gamekeeper.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.data.local.database.PersistentCounterEntity
import io.github.m0nkeysan.gamekeeper.core.model.Player
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CounterItem(
    val id: String,
    val name: String,
    val count: Int,
    val color: Long
)

data class CounterUiState(
    val counters: List<CounterItem> = emptyList()
)

class CounterViewModel : ViewModel() {
    private val counterRepository = PlatformRepositories.getCounterRepository()
    
    private val _state = MutableStateFlow(CounterUiState())
    val state = _state.asStateFlow()

    private val funnyNames = listOf(
        "Unicorn", "Dragon", "Potato", "Ninja", "Pirate", "Wizard", "Robot", "Alien", "Ghost", "Cactus",
        "Banana", "Taco", "Pizza", "Cookie", "Donut", "Muffin", "Pancake", "Waffle", "Burger", "Fries"
    )
    
    private val playerTimestamps = mutableMapOf<String, Long>()

    init {
        loadCounters()
    }

    private fun loadCounters() {
        viewModelScope.launch {
            counterRepository.getAllCounters().collect { entities ->
                val counters = entities.map { entity ->
                    playerTimestamps[entity.id] = entity.updatedAt
                    CounterItem(
                        id = entity.id,
                        name = entity.name,
                        count = entity.count,
                        color = entity.color
                    )
                }
                _state.update { it.copy(counters = counters) }
            }
        }
    }

    fun addRandomCounter() {
        val randomName = "${funnyNames.random()} ${Random.nextInt(1, 99)}"
        val id = Random.nextLong().toString()
        val color = generateRandomPastelColor()
        val timestamp = System.currentTimeMillis()
        val nextOrder = _state.value.counters.size
        
        viewModelScope.launch {
            counterRepository.addCounter(
                PersistentCounterEntity(
                    id = id,
                    name = randomName,
                    count = 0,
                    color = color,
                    updatedAt = timestamp,
                    sortOrder = nextOrder
                )
            )
        }
    }
    
    fun updateCounter(id: String, name: String, count: Int, color: Long) {
        val originalTimestamp = playerTimestamps[id] ?: System.currentTimeMillis()
        val currentOrder = _state.value.counters.indexOfFirst { it.id == id }.takeIf { it != -1 } ?: 0
        
        viewModelScope.launch {
            counterRepository.updateCounter(
                PersistentCounterEntity(
                    id = id,
                    name = name,
                    count = count,
                    color = color,
                    updatedAt = originalTimestamp,
                    sortOrder = currentOrder
                )
            )
        }
    }

    fun reorderCounters(newOrderIds: List<String>) {
        viewModelScope.launch {
            newOrderIds.forEachIndexed { index, id ->
                counterRepository.updateOrder(id, index)
            }
        }
    }
    
    fun deleteCounter(id: String) {
        viewModelScope.launch {
            counterRepository.deleteCounter(id)
            // Re-normalize orders
            _state.value.counters.filter { it.id != id }.forEachIndexed { index, counter ->
                counterRepository.updateOrder(counter.id, index)
            }
        }
    }

    private fun generateRandomPastelColor(): Long {
        val hue = Random.nextFloat() * 360f
        val saturation = 0.4f + Random.nextFloat() * 0.2f 
        val value = 0.9f + Random.nextFloat() * 0.1f 
        val color = Color.hsv(hue, saturation, value)
        return (0xFF000000 or (color.red * 255).toLong().shl(16) or (color.green * 255).toLong().shl(8) or (color.blue * 255).toLong())
    }

    fun incrementCount(playerId: String) {
        val counter = _state.value.counters.find { it.id == playerId } ?: return
        viewModelScope.launch {
            counterRepository.updateCount(playerId, counter.count + 1)
        }
    }

    fun decrementCount(playerId: String) {
        val counter = _state.value.counters.find { it.id == playerId } ?: return
        viewModelScope.launch {
            counterRepository.updateCount(playerId, counter.count - 1)
        }
    }

    fun adjustCount(playerId: String, amount: Int) {
        val counter = _state.value.counters.find { it.id == playerId } ?: return
        viewModelScope.launch {
            counterRepository.updateCount(playerId, counter.count + amount)
        }
    }

    fun setCount(playerId: String, newCount: Int) {
        viewModelScope.launch {
            counterRepository.updateCount(playerId, newCount)
        }
    }

    fun reset() {}
}
