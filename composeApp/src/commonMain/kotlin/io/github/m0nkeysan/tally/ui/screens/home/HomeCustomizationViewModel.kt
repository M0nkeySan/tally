package io.github.m0nkeysan.tally.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.domain.model.HomeFeatureState
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeCustomizationViewModel : ViewModel() {
    private val userPreferencesRepository = PlatformRepositories.getUserPreferencesRepository()

    private val _featureStates = MutableStateFlow<List<HomeFeatureState>>(emptyList())
    val featureStates: StateFlow<List<HomeFeatureState>> = _featureStates.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private var originalStates: List<HomeFeatureState> = emptyList()

    init {
        loadFeatureStates()
    }

    private fun loadFeatureStates() {
        viewModelScope.launch {
            val states = userPreferencesRepository.getHomeFeatureStates().first()
            originalStates = states
            _featureStates.value = states
            _hasChanges.value = false
        }
    }

    fun toggleFeature(featureId: String) {
        val currentStates = _featureStates.value
        val updatedStates = currentStates.map { state ->
            if (state.featureId == featureId) {
                state.copy(enabled = !state.enabled)
            } else {
                state
            }
        }
        _featureStates.value = updatedStates
        updateHasChanges()
    }

    fun reorderFeatures(newOrderIds: List<String>) {
        val currentStates = _featureStates.value
        val statesMap = currentStates.associateBy { it.featureId }

        val updatedStates = newOrderIds.mapIndexedNotNull { index, id ->
            statesMap[id]?.copy(order = index)
        }

        _featureStates.value = updatedStates
        updateHasChanges()
    }

    suspend fun saveChanges(): Boolean {
        return try {
            userPreferencesRepository.saveHomeFeatureStates(_featureStates.value)
            originalStates = _featureStates.value
            _hasChanges.value = false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun discardChanges() {
        _featureStates.value = originalStates
        _hasChanges.value = false
    }

    private fun updateHasChanges() {
        _hasChanges.value = _featureStates.value != originalStates
    }
}
