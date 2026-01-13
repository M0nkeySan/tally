package io.github.m0nkeysan.gamekeeper.ui.screens.dice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.core.model.DiceConfiguration
import io.github.m0nkeysan.gamekeeper.core.model.DiceRoll
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class DiceRollerViewModel : ViewModel() {
    private val userPreferencesRepository = PlatformRepositories.getUserPreferencesRepository()

    // Configuration state
    private val _configuration = MutableStateFlow(DiceConfiguration())
    val configuration: StateFlow<DiceConfiguration> = _configuration.asStateFlow()

    // Current roll result
    private val _currentRoll = MutableStateFlow<DiceRoll?>(null)
    val currentRoll: StateFlow<DiceRoll?> = _currentRoll.asStateFlow()

    // Rolling animation state
    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

    init {
         // Load saved configuration
         viewModelScope.launch {
             userPreferencesRepository.getDiceConfiguration()
                 .collect { config ->
                     _configuration.value = config
                     // Generate an initial static result so the box isn't empty on start
                     if (_currentRoll.value == null) {
                         generateRoll(config)
                     }
                 }
         }
     }

    fun rollDice() {
         if (_isRolling.value) return

         viewModelScope.launch {
             _isRolling.value = true
             val config = _configuration.value

             if (config.animationEnabled) {
                 // Digital Scramble Effect:
                 // Rapidly cycle numbers to simulate calculation/chaos
                 repeat(DiceConstants.SCRAMBLE_ITERATIONS) {
                     generateRoll(config)
                     delay(DiceConstants.SCRAMBLE_DELAY_MS)
                 }
             }

             // Final Result (The one that counts)
             generateRoll(config)
             _isRolling.value = false
         }
     }

     private fun generateRoll(config: DiceConfiguration) {
         val results = List(config.numberOfDice) {
             Random.nextInt(1, config.diceType.sides + 1)
         }
         _currentRoll.value = DiceRoll(
             individualResults = results,
             total = results.sum()
         )
     }

     fun updateConfiguration(config: DiceConfiguration) {
         viewModelScope.launch {
             _configuration.value = config
             userPreferencesRepository.saveDiceConfiguration(config)
             // Reset roll when config changes to avoid confusion
             generateRoll(config)
         }
     }
}