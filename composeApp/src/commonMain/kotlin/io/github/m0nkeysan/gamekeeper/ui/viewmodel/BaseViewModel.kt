package io.github.m0nkeysan.gamekeeper.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class BaseViewModel {
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    open fun onCleared() {
        viewModelScope.cancel()
    }
}

abstract class StateViewModel<T>(initialState: T) : BaseViewModel() {
    var state by mutableStateOf(initialState)
        protected set
    
    protected fun updateState(newState: T) {
        state = newState
    }
}
