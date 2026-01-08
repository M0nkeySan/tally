package io.github.m0nkeysan.gamekeeper.ui.strings

/**
 * Centralized string resources for the GameKeeper application.
 * All user-facing strings should be defined here for easy i18n (internationalization) support.
 * 
 * This approach allows for:
 * - Easy translation to other languages
 * - Consistent terminology across the app
 * - Centralized string management
 * - Future integration with proper i18n frameworks
 */
object AppStrings {
    // Home Screen
    const val HOME_TITLE = "GameKeeper"
    
    // Counter Screen
    const val COUNTER_TITLE = "Counter"
    const val COUNTER_SETTINGS = "Settings"
    const val COUNTER_REINITIALIZE_ALL = "Reinitialise all"
    const val COUNTER_DELETE_EVERYTHING = "Delete everything"
    const val COUNTER_EMPTY_STATE = "Tap + to add a counter"
    const val COUNTER_SETTINGS_TITLE = "Counter Settings"
    const val COUNTER_RESET_ALL = "Reset All Counters"
    const val COUNTER_REMOVE_LABEL = "REMOVE (-)"
    const val COUNTER_ADD_LABEL = "ADD (+)"
    
    // Player Selection Screen
    const val PLAYERS_NO_PLAYERS = "No players found. Add one!"
    
    // Edit Counter Screen
    const val EDIT_COUNTER_TITLE = "Edit Counter"
    const val EDIT_COUNTER_SAVE = "SAVE CHANGES"
    
    // Common Actions
    const val ACTION_DELETE = "Delete"
    const val ACTION_CANCEL = "Cancel"
    const val ACTION_SAVE = "Save"
    const val ACTION_BACK = "Back"
    const val ACTION_CREATE = "Create"
    const val ACTION_EDIT = "Edit"
    
    // Loading and States
    const val STATE_LOADING = "Loading..."
    const val STATE_LOADING_GAMES = "Loading games..."
    const val STATE_EMPTY_GAMES = "No games yet. Create one!"
    const val STATE_ERROR = "An error occurred"
    
    // Game Specific
    const val GAME_TAROT = "Tarot"
    const val GAME_YAHTZEE = "Yahtzee"
    const val GAME_FINGER_SELECTOR = "Finger Selector"
    const val GAME_COUNTER = "Counter"
    
    // Descriptions
    const val DESC_TAROT = "Score Tarot games for 3, 4, or 5 players"
    const val DESC_YAHTZEE = "Complete Yahtzee scorecard with automatic bonuses"
    const val DESC_FINGER_SELECTOR = "Randomly select a starting player with multi-touch"
    const val DESC_COUNTER = "Simple counter for any board game"
}
