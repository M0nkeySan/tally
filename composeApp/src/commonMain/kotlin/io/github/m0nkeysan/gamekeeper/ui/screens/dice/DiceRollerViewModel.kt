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

/**
 * ViewModel for Dice Roller screen
 * 
 * Manages:
 * - Dice configuration (number, type, animation, shake)
 * - Roll logic and animation timing
 * - Persistence of user preferences
 */
class DiceRollerViewModel : ViewModel() {
    private val userPreferencesRepository = PlatformRepositories.getUserPreferencesRepository()
    
    // Configuration state - loaded from preferences
    private val _configuration = MutableStateFlow(DiceConfiguration())
    val configuration: StateFlow<DiceConfiguration> = _configuration.asStateFlow()
    
    // Current roll result
    private val _currentRoll = MutableStateFlow<DiceRoll?>(null)
    val currentRoll: StateFlow<DiceRoll?> = _currentRoll.asStateFlow()
    
    // Rolling animation state
    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()
    
    init {
        // Load saved configuration from preferences
        viewModelScope.launch {
            userPreferencesRepository.getDiceConfiguration()
                .collect { config ->
                    _configuration.value = config
                }
        }
    }
    
    /**
     * Execute a dice roll with animation
     * 
     * Animation flow:
     * 1. Set isRolling = true (triggers 2D rotation animation)
     * 2. Wait for animation duration (800ms if enabled)
     * 3. Generate random results
     * 4. Create DiceRoll object
     * 5. Set currentRoll (displays results)
     * 6. Set isRolling = false
     */
    fun rollDice() {
        // Prevent concurrent rolls
        if (_isRolling.value) return
        
        viewModelScope.launch {
            _isRolling.value = true
            
            // Simulate animation delay if enabled
            // Gives visual feedback that something is happening
            if (_configuration.value.animationEnabled) {
                delay(800) // 2D rotation animation duration
            }
            
            // Generate random results for each die
            val results = List(_configuration.value.numberOfDice) {
                Random.nextInt(1, _configuration.value.diceType.sides + 1)
            }
            
            // Create roll object
            val roll = DiceRoll(
                individualResults = results,
                total = results.sum()
            )
            
            // Update state with results
            _currentRoll.value = roll
            
            // Animation complete
            _isRolling.value = false
        }
    }
    
    /**
     * Update dice configuration and save to preferences
     * 
     * @param config New configuration to save
     */
    fun updateConfiguration(config: DiceConfiguration) {
        viewModelScope.launch {
            _configuration.value = config
            userPreferencesRepository.saveDiceConfiguration(config)
        }
    }
}
