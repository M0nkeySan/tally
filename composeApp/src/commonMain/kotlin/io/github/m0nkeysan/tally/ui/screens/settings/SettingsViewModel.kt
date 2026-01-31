package io.github.m0nkeysan.tally.ui.screens.settings// SettingsViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.m0nkeysan.tally.core.domain.model.AppTheme
import io.github.m0nkeysan.tally.core.utils.getCurrentTimeMillis
import io.github.m0nkeysan.tally.platform.PlatformRepositories
import io.github.m0nkeysan.tally.platform.getFileSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class FeedbackType {
    SUCCESS,
    ERROR
}

data class FeedbackMessage(
    val messageKey: String,
    val type: FeedbackType
)

class SettingsViewModel() : ViewModel() {

    private val preferencesRepository = PlatformRepositories.getUserPreferencesRepository()
    private val localeManager = PlatformRepositories.getLocaleManager()
    private val databaseExporter = PlatformRepositories.getDatabaseExporter()
    private val fileSaver = getFileSaver()
    
    private val _feedbackMessage = MutableStateFlow<FeedbackMessage?>(null)
    val feedbackMessage: StateFlow<FeedbackMessage?> = _feedbackMessage

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.getTheme(),
        localeManager.currentLocale
    ) { theme, localeCode ->
        SettingsUiState(
            currentTheme = theme,
            currentLocaleCode = localeCode,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(isLoading = true)
    )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.saveTheme(theme)
        }
    }

    fun setLocale(localeCode: String) {
        localeManager.setLocale(localeCode) 
    }
    
    fun exportDatabase() {
        viewModelScope.launch {
            try {
                // Export database to backup object
                val backup = databaseExporter.exportToBackup()
                
                // Serialize to JSON
                val json = Json {
                    prettyPrint = true
                    encodeDefaults = true
                }
                val jsonString = json.encodeToString(backup)
                
                // Generate filename with timestamp
                val timestamp = getCurrentTimeMillis()
                val filename = "gamekeeper_backup_$timestamp.json"
                
                // Save file using platform-specific file saver
                fileSaver.saveJsonFile(filename, jsonString)
                    .onSuccess {
                        _feedbackMessage.value = FeedbackMessage(
                            messageKey = "export_success",
                            type = FeedbackType.SUCCESS
                        )
                    }
                    .onFailure { error ->
                        _feedbackMessage.value = FeedbackMessage(
                            messageKey = "export_error",
                            type = FeedbackType.ERROR
                        )
                    }
            } catch (e: Exception) {
                _feedbackMessage.value = FeedbackMessage(
                    messageKey = "export_error",
                    type = FeedbackType.ERROR
                )
            }
        }
    }
    
    fun importDatabase() {
        viewModelScope.launch {
            try {
                // Pick and read file using platform-specific file saver
                fileSaver.pickJsonFile()
                    .onSuccess { jsonString ->
                        try {
                            // Parse JSON
                            val json = Json {
                                ignoreUnknownKeys = true
                            }
                            val backup = json.decodeFromString<io.github.m0nkeysan.tally.core.domain.backup.DatabaseBackup>(jsonString)
                            
                            // Import to database
                            databaseExporter.importFromBackup(backup)
                            
                            _feedbackMessage.value = FeedbackMessage(
                                messageKey = "import_success",
                                type = FeedbackType.SUCCESS
                            )
                        } catch (e: Exception) {
                            _feedbackMessage.value = FeedbackMessage(
                                messageKey = "import_error",
                                type = FeedbackType.ERROR
                            )
                        }
                    }
                    .onFailure { error ->
                        _feedbackMessage.value = FeedbackMessage(
                            messageKey = "import_error",
                            type = FeedbackType.ERROR
                        )
                    }
            } catch (e: Exception) {
                _feedbackMessage.value = FeedbackMessage(
                    messageKey = "import_error",
                    type = FeedbackType.ERROR
                )
            }
        }
    }
    
    fun clearFeedbackMessage() {
        _feedbackMessage.value = null
    }
}

data class SettingsUiState(
    val currentTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val currentLocaleCode: String = "en",
    val isLoading: Boolean = false
)