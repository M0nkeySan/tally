package io.github.m0nkeysan.tally.ui.screens.counter

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.model.Counter
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.core.model.MergedCounterChange
import io.github.m0nkeysan.tally.platform.PlatformRepositories
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

enum class CounterDisplayMode {
    MOST_POINTS, LEAST_POINTS
}

data class CounterUiState(
    val counters: List<CounterItem> = emptyList(),
    val displayMode: CounterDisplayMode = CounterDisplayMode.MOST_POINTS
)

class CounterViewModel : ViewModel() {
    private val counterRepository = PlatformRepositories.getCounterRepository()
    private val prefsRepository = PlatformRepositories.getUserPreferencesRepository()
    
    companion object {
        private const val KEY_DISPLAY_MODE = "counter_display_mode"
    }

    private val _state = MutableStateFlow(CounterUiState())
    val state = _state.asStateFlow()
    
    private val _mergedHistory = MutableStateFlow<List<MergedCounterChange>>(emptyList())
    val mergedHistory: StateFlow<List<MergedCounterChange>> = _mergedHistory.asStateFlow()

    private val funnyNames = listOf(
        "Unicorn", "Dragon", "Potato", "Ninja", "Pirate", "Wizard", "Robot", "Alien", "Ghost", "Cactus",
        "Banana", "Taco", "Pizza", "Cookie", "Donut", "Muffin", "Pancake", "Waffle", "Burger", "Fries"
    )
    
    private val playerTimestamps = mutableMapOf<String, Long>()

    init {
        loadCounters()
        loadPreferences()
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            counterRepository.getMergedCounterHistory().collect { history ->
                _mergedHistory.value = history
            }
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            prefsRepository.getString(KEY_DISPLAY_MODE, CounterDisplayMode.MOST_POINTS.name)
                .collect { modeName ->
                    val mode = try {
                        CounterDisplayMode.valueOf(modeName)
                    } catch (e: Exception) {
                        CounterDisplayMode.MOST_POINTS
                    }
                    _state.update { it.copy(displayMode = mode) }
                }
        }
    }

    fun setDisplayMode(mode: CounterDisplayMode) {
        viewModelScope.launch {
            prefsRepository.saveString(KEY_DISPLAY_MODE, mode.name)
        }
    }

    private fun loadCounters() {
        viewModelScope.launch {
            counterRepository.getAllCounters().collect { counters ->
                val counterItems = counters.map { counter ->
                    playerTimestamps[counter.id] = counter.updatedAt
                    CounterItem(
                        id = counter.id,
                        name = counter.name,
                        count = counter.count,
                        color = counter.color
                    )
                }
                _state.update { it.copy(counters = counterItems) }
            }
        }
    }

    fun addRandomCounter() {
        val randomName = "${funnyNames.random()} ${Random.nextInt(1, 99)}"
        val color = generateRandomPastelColor()
        val nextOrder = _state.value.counters.size
        
        viewModelScope.launch {
            counterRepository.addCounter(
                Counter.create(
                    name = randomName,
                    count = 0,
                    color = color,
                    sortOrder = nextOrder
                )
            )
        }
    }
    
    fun updateCounter(id: String, name: String, count: Int, color: Long) {
        val originalTimestamp = playerTimestamps[id] ?: getCurrentTimeMillis()
        val currentOrder = _state.value.counters.indexOfFirst { it.id == id }.takeIf { it != -1 } ?: 0
        
        viewModelScope.launch {
            counterRepository.updateCounter(
                Counter(
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
        val counter = _state.value.counters.find { it.id == id } ?: return
        playerTimestamps.remove(id)
        viewModelScope.launch {
            // Log the deletion as a history entry
            counterRepository.logCounterDeletion(
                counterId = id,
                counterName = counter.name,
                counterColor = counter.color
            )
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

        // Ensure you are using the Compose Color class correctly
        val color = Color.hsv(hue, saturation, value)

        // Safely convert to Long (ARGB format)
        // Note: 0xFF000000L ensures we are working with Longs
        val alpha = 0xFF000000L
        val red = (color.red * 255).toLong().shl(16)
        val green = (color.green * 255).toLong().shl(8)
        val blue = (color.blue * 255).toLong()

        return alpha or red or green or blue
    }

    fun incrementCount(playerId: String) {
        val counter = _state.value.counters.find { it.id == playerId } ?: return
        val newCount = counter.count + 1
        viewModelScope.launch {
            counterRepository.logCounterChange(
                counterId = playerId,
                counterName = counter.name,
                counterColor = counter.color,
                previousValue = counter.count,
                newValue = newCount
            )
            counterRepository.updateCount(playerId, newCount)
        }
    }

    fun decrementCount(playerId: String) {
        val counter = _state.value.counters.find { it.id == playerId } ?: return
        val newCount = counter.count - 1
        viewModelScope.launch {
            counterRepository.logCounterChange(
                counterId = playerId,
                counterName = counter.name,
                counterColor = counter.color,
                previousValue = counter.count,
                newValue = newCount
            )
            counterRepository.updateCount(playerId, newCount)
        }
    }

    fun adjustCount(playerId: String, amount: Int) {
        val counter = _state.value.counters.find { it.id == playerId } ?: return
        val newCount = counter.count + amount
        viewModelScope.launch {
            counterRepository.logCounterChange(
                counterId = playerId,
                counterName = counter.name,
                counterColor = counter.color,
                previousValue = counter.count,
                newValue = newCount
            )
            counterRepository.updateCount(playerId, newCount)
        }
    }

    fun setCount(playerId: String, newCount: Int) {
        val counter = _state.value.counters.find { it.id == playerId } ?: return
        viewModelScope.launch {
            counterRepository.logCounterChange(
                counterId = playerId,
                counterName = counter.name,
                counterColor = counter.color,
                previousValue = counter.count,
                newValue = newCount
            )
            counterRepository.updateCount(playerId, newCount)
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            counterRepository.resetAllCounts()
            counterRepository.clearCounterHistory()
        }
    }

    fun deleteAll() {
        playerTimestamps.clear()
        viewModelScope.launch {
            counterRepository.deleteAllCounters()
            counterRepository.clearCounterHistory()
        }
    }

    fun clearCounterHistory() {
        viewModelScope.launch {
            counterRepository.clearCounterHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerTimestamps.clear()
    }
}
