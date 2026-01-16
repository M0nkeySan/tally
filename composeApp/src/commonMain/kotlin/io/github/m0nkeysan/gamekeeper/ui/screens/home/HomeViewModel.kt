package io.github.m0nkeysan.gamekeeper.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.gamekeeper.platform.PlatformRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val userPreferencesRepository = PlatformRepositories.getUserPreferencesRepository()

    private val _cardOrder = MutableStateFlow(defaultCardOrder)
    val cardOrder: StateFlow<List<String>> = _cardOrder.asStateFlow()

    init {
        loadCardOrder()
    }

    private fun loadCardOrder() {
        viewModelScope.launch {
            userPreferencesRepository.getCardOrder().collect { savedOrder ->
                if (savedOrder != null && savedOrder.isNotEmpty()) {
                    // Reorder based on saved order, keeping any new cards at the end
                    val orderedIds = savedOrder.toMutableList()
                    val missingIds = defaultCardOrder.filter { it !in orderedIds }
                    orderedIds.addAll(missingIds)
                    // Remove any ids that no longer exist
                    val validIds = orderedIds.filter { it in defaultCardOrder }
                    _cardOrder.value = validIds
                } else {
                    _cardOrder.value = defaultCardOrder
                }
            }
        }
    }

    fun updateCardOrder(newOrder: List<String>) {
        _cardOrder.value = newOrder
        viewModelScope.launch {
            userPreferencesRepository.saveCardOrder(newOrder)
        }
    }

    companion object {
        val defaultCardOrder = listOf(
            "finger_selector",
            "tarot",
            "yahtzee",
            "counter",
            "dice_roller"
        )
    }
}
