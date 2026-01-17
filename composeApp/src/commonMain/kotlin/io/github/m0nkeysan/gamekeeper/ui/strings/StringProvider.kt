package io.github.m0nkeysan.gamekeeper.ui.strings

/**
 * Interface for string localization support.
 * Each language implementation provides all user-facing strings.
 *
 * Implementations:
 * - [AppStringsEn] - English strings
 * - [AppStringsFr] - French strings
 */
interface StringProvider {
    // Home Screen
    val HOME_TITLE: String

    // Counter Screen
    val COUNTER_TITLE: String
    val COUNTER_SETTINGS: String
    val COUNTER_REINITIALIZE_ALL: String
    val COUNTER_DELETE_EVERYTHING: String
    val COUNTER_EMPTY_STATE: String
    val COUNTER_REMOVE_LABEL: String
    val COUNTER_ADD_LABEL: String

    // Player Selection Screen
    val PLAYERS_NO_PLAYERS: String

    // Common Actions
    val ACTION_DELETE: String
    val ACTION_DELETE_ALL: String
    val ACTION_CANCEL: String
    val ACTION_SAVE: String
    val ACTION_BACK: String
    val ACTION_CREATE: String
    val ACTION_RESET: String

    // Loading and States
    val STATE_LOADING: String
    val STATE_LOADING_GAMES: String

    // Game Specific
    val GAME_TAROT: String
    val GAME_YAHTZEE: String
    val GAME_FINGER_SELECTOR: String
    val GAME_COUNTER: String

    // Descriptions
    val DESC_TAROT: String
    val DESC_YAHTZEE: String
    val DESC_COUNTER: String

    // Game Operations
    val GAME_DELETE_ALL_TITLE: String
    val GAME_DELETE_ALL_CONFIRM: String
    val GAME_CREATE: String

    // Dice Roller
    val GAME_DICE: String
    val DESC_DICE: String

    // Common Actions (extended)
    val ACTION_RETRY: String
    val ACTION_OK: String

    // Common Content Descriptions (Accessibility)
    val CD_BACK: String
    val CD_SETTINGS: String
    val CD_ADD: String
    val CD_MENU: String
    val CD_ADD_PLAYER: String
    val CD_TOGGLE_COLLAPSE: String
    val CD_TOGGLE_EXPAND: String
    val CD_PLAYER: String
    val CD_GAME_ICON: String
    val CD_YAHTZEE_ICON: String
    val CD_TAROT_ICON: String
    val CD_FINISHED_GAME: String
    val CD_REMOVE_PLAYER: String

    // Common Dialogs
    val DIALOG_DELETE_ALL_TITLE: String
    val DIALOG_DELETE_COUNTER_TITLE: String
    val DIALOG_DELETE_COUNTER_MESSAGE: String
    val DIALOG_DEACTIVATE_PLAYER: String
    val DIALOG_DELETE_PLAYER: String

    // Tarot - Statistics
    val TAROT_STATS_TAB_CURRENT_GAME: String
    val TAROT_STATS_TAB_PLAYER_STATS: String
    val TAROT_STATS_LABEL_CALLED: String
    val TAROT_STATS_LABEL_AS_TAKER: String
    val TAROT_STATS_LABEL_WIN_RATE: String
    val TAROT_STATS_LABEL_FAVORITE_BID: String
    val TAROT_STATS_LABEL_MOST_SUCCESSFUL: String
    val TAROT_STATS_SECTION_PERFORMANCE_DETAILS: String
    val TAROT_STATS_LABEL_AVG_BOUTS: String
    val TAROT_STATS_LABEL_AVG_WON: String
    val TAROT_STATS_LABEL_AVG_LOST: String
    val TAROT_STATS_SECTION_CALLED_PERFORMANCE: String
    val TAROT_STATS_ERROR_TITLE: String

    // Tarot - Scoring
    val TAROT_SCORING_SCREEN_TITLE: String
    val TAROT_SCORING_CD_ADD_ROUND: String
    val TAROT_SCORING_SECTION_SCORES: String
    val TAROT_SCORING_SECTION_HISTORY: String
    val TAROT_SCORING_EMPTY_ROUNDS: String
    val TAROT_SCORING_ROUND_TITLE: String
    val TAROT_SCORING_ROUND_DETAILS: String
    val TAROT_SCORING_ANNOUNCE_PETIT_AU_BOUT: String
    val TAROT_SCORING_ANNOUNCE_POIGNEE: String
    val TAROT_SCORING_ANNOUNCE_CHELEM: String
    val TAROT_SCORING_GAME_TITLE: String

    // Tarot - Round Addition
    val TAROT_ROUND_ACTION_SAVE: String
    val TAROT_ROUND_SECTION_PLAYERS: String
    val TAROT_ROUND_LABEL_TAKER: String
    val TAROT_ROUND_LABEL_CALLED_PLAYER: String
    val TAROT_ROUND_SECTION_BID: String
    val TAROT_ROUND_SECTION_BOUTS: String
    val TAROT_ROUND_LABEL_BOUTS: String
    val TAROT_ROUND_SECTION_POINTS: String
    val TAROT_ROUND_FIELD_ATTACKER_SCORE: String
    val TAROT_ROUND_LABEL_DEFENSE: String
    val TAROT_ROUND_CONTRACT_WON: String
    val TAROT_ROUND_CONTRACT_LOST: String
    val TAROT_ROUND_SECTION_ANNOUNCES: String
    val TAROT_ROUND_ANNOUNCE_PETIT_AU_BOUT: String
    val TAROT_ROUND_ANNOUNCE_POIGNEE: String
    val TAROT_ROUND_LABEL_CHELEM: String

    // Counter - Main Screen
    val COUNTER_CD_ADD: String
    val COUNTER_CD_HISTORY: String
    val COUNTER_CD_RESET_ALL: String
    val COUNTER_CD_DELETE_ALL: String
    val COUNTER_DIALOG_SETTINGS_TITLE: String
    val COUNTER_DIALOG_SETTINGS_LABEL: String
    val COUNTER_SETTINGS_OPTION_MOST: String
    val COUNTER_SETTINGS_OPTION_LEAST: String
    val COUNTER_ACTION_CLOSE: String
    val COUNTER_DIALOG_RESET_TITLE: String
    val COUNTER_DIALOG_RESET_MESSAGE: String
    val COUNTER_DIALOG_DELETE_TITLE: String
    val COUNTER_DIALOG_DELETE_MESSAGE: String
    val COUNTER_ACTION_DELETE: String
    val COUNTER_DIALOG_ADJUST_LABEL: String
    val COUNTER_DIALOG_ADJUST_PLACEHOLDER: String
    val COUNTER_DIALOG_SET_SCORE_LABEL: String
    val COUNTER_CD_DECREASE: String
    val COUNTER_CD_INCREASE: String
    val COUNTER_LEADER_DISPLAY_FORMAT: String

    // Counter - History & Edit
    val COUNTER_HISTORY_TITLE: String
    val COUNTER_HISTORY_SUBTITLE: String
    val COUNTER_HISTORY_CD_DELETE: String
    val COUNTER_HISTORY_EMPTY: String
    val COUNTER_HISTORY_DELETED_EMOJI: String
    val COUNTER_HISTORY_DELETED_TEXT: String
    val COUNTER_EDIT_TITLE: String
    val COUNTER_EDIT_CD_DELETE: String
    val COUNTER_EDIT_ACTION_SAVE: String
    val COUNTER_EDIT_LABEL_COLOR: String
    val COUNTER_EDIT_FIELD_NAME: String
    val COUNTER_EDIT_PLACEHOLDER_NAME: String
    val COUNTER_EDIT_FIELD_VALUE: String
    val COUNTER_EDIT_PLACEHOLDER_VALUE: String

    // Yahtzee - Scoring
    val YAHTZEE_SCORING_CD_PREVIOUS: String
    val YAHTZEE_SCORING_TURN_INDICATOR: String
    val YAHTZEE_SCORING_FALLBACK_NAME: String
    val YAHTZEE_SCORING_CD_DROPDOWN: String
    val YAHTZEE_SCORING_TOTAL_FORMAT: String
    val YAHTZEE_SCORING_CD_NEXT: String
    val YAHTZEE_SCORING_TURN_LABEL: String
    val YAHTZEE_SCORING_VIEWING_LABEL: String
    val YAHTZEE_SECTION_UPPER: String
    val YAHTZEE_SECTION_LOWER: String
    val YAHTZEE_DIALOG_SELECT_SCORE: String
    val YAHTZEE_DIALOG_ENTER_SCORE: String
    val YAHTZEE_PLACEHOLDER_DICE_SUM: String
    val YAHTZEE_ERROR_SCORE_TOO_HIGH: String
    val YAHTZEE_LABEL_UPPER_BONUS: String
    val YAHTZEE_LABEL_BONUS_NEEDED: String
    val YAHTZEE_LABEL_BONUS_EARNED: String
    val YAHTZEE_LABEL_TOTAL_SCORE: String
    val YAHTZEE_GAME_TITLE: String
    val YAHTZEE_NEW_GAME_TITLE: String
    val YAHTZEE_GAME_NAME_DEFAULT: String

    // Dice Roller
    val DICE_DISPLAY_FORMAT: String
    val DICE_ROLLS_FORMAT: String
    val DICE_TOTAL_FORMAT: String
    val DICE_INSTRUCTION_TAP: String
    val DICE_DIALOG_TITLE: String
    val DICE_FIELD_NUMBER: String
    val DICE_FIELD_TYPE: String
    val DICE_FIELD_CUSTOM: String
    val DICE_ERROR_NOT_VALID: String
    val DICE_ERROR_MIN_SIDES: String
    val DICE_ERROR_MAX_SIDES: String
    val DICE_PLACEHOLDER_CUSTOM: String
    val DICE_SETTING_ANIMATION: String
    val DICE_SETTING_SHAKE: String

    // Finger Selector
    val FINGER_SELECTOR_DIALOG_TITLE: String
    val FINGER_SELECTOR_SECTION_MODE: String
    val FINGER_SELECTOR_MODE_FINGERS: String
    val FINGER_SELECTOR_MODE_GROUPS: String
    val FINGER_SELECTOR_CD_TOUCH: String
    val FINGER_SELECTOR_INSTRUCTION_PLACE: String
    val FINGER_SELECTOR_INSTRUCTION_WAIT: String
    val FINGER_SELECTOR_SLIDER_VALUE_FORMAT: String

    // Game Selection & Creation
    val GAME_SELECTION_CD_STATISTICS: String
    val GAME_SELECTION_CD_CREATE: String
    val GAME_SELECTION_CD_DELETE_ALL: String
    val GAME_SELECTION_LOADING: String
    val GAME_SELECTION_EMPTY: String
    val GAME_SELECTION_CD_DELETE_GAME: String
    val GAME_CREATION_ACTION_CANCEL: String
    val GAME_CREATION_ACTION_CREATE: String
    val GAME_CREATION_NEW_TAROT_TITLE: String
    val GAME_CREATION_TAROT_NAME_DEFAULT: String
    val GAME_CREATION_FIELD_GAME_NAME: String
    val GAME_DELETION_DIALOG_TAROT_TITLE: String
    val GAME_DELETION_DIALOG_TAROT_MESSAGE: String
    val GAME_DELETION_DIALOG_YAHTZEE_TITLE: String
    val GAME_DELETION_DIALOG_YAHTZEE_MESSAGE: String

    // Player Management
    val PLAYER_SECTION_PLAYERS: String
    val PLAYER_SECTION_DEACTIVATED: String
    val PLAYER_FIELD_NAME: String
    val PLAYER_PLACEHOLDER_NAME: String
    val PLAYER_ERROR_NAME_TAKEN: String
    val PLAYER_LABEL_COLOR: String
    val PLAYER_CD_EDIT: String

    // Home & Results
    val HOME_CD_GAMES: String
    val HOME_CD_PLAYERS: String
    val HOME_CD_FINGER_SELECTOR: String
    val HOME_TITLE_FINGER_SELECTOR: String
    val HOME_DESC_FINGER_SELECTOR: String
    val HOME_CD_COUNTER: String
    val HOME_CD_DICE: String
    val RESULTS_ACTION_BACK: String
    val RESULTS_TITLE_TIE: String
    val RESULTS_TITLE_WINNER: String
    val RESULTS_SECTION_SCORES: String

    // Yahtzee - Statistics Screen
    val YAHTZEE_STATS_GLOBAL: String
    val YAHTZEE_STATS_SELECT_PLAYER: String
    val YAHTZEE_STATS_NO_DATA: String

    // Player Statistics Sections
    val YAHTZEE_STATS_OVERALL_PERFORMANCE: String
    val YAHTZEE_STATS_SCORE_BOX: String
    val YAHTZEE_STATS_RECENT_GAMES: String

    // Player Statistics Labels
    val YAHTZEE_STATS_TOTAL_GAMES: String
    val YAHTZEE_STATS_FINISHED_GAMES: String
    val YAHTZEE_STATS_WINS: String
    val YAHTZEE_STATS_AVERAGE_SCORE: String
    val YAHTZEE_STATS_PERSONAL_BEST: String
    val YAHTZEE_STATS_TOTAL_YAHTZEES: String
    val YAHTZEE_STATS_YAHTZEE_RATE: String
    val YAHTZEE_STATS_UPPER_SECTION: String
    val YAHTZEE_STATS_LOWER_SECTION: String
    val YAHTZEE_STATS_BONUS_RATE: String

    // Global Statistics Sections
    val YAHTZEE_STATS_GLOBAL_OVERALL: String
    val YAHTZEE_STATS_GLOBAL_FUN_FACTS: String
    val YAHTZEE_STATS_GLOBAL_LEADERBOARDS: String
    val YAHTZEE_STATS_GLOBAL_CATEGORY: String

    // Global Statistics Labels
    val YAHTZEE_STATS_GLOBAL_FINISHED: String
    val YAHTZEE_STATS_GLOBAL_TOTAL_PLAYERS: String
    val YAHTZEE_STATS_GLOBAL_MOST_ACTIVE: String
    val YAHTZEE_STATS_GLOBAL_HIGH_SCORE: String
    val YAHTZEE_STATS_GLOBAL_AVERAGE_SCORE: String
    val YAHTZEE_STATS_GLOBAL_TOTAL_YAHTZEES: String
    val YAHTZEE_STATS_GLOBAL_YAHTZEE_RATE: String
    val YAHTZEE_STATS_GLOBAL_MOST_YAHTZEES_GAME: String
    val YAHTZEE_STATS_GLOBAL_UPPER_BONUS: String
    val YAHTZEE_STATS_GLOBAL_DICE_ROLLS: String
    val YAHTZEE_STATS_GLOBAL_POINTS_SCORED: String
    val YAHTZEE_STATS_GLOBAL_AVG_PLAYERS: String
    val YAHTZEE_STATS_GLOBAL_LUCKIEST: String
    val YAHTZEE_STATS_GLOBAL_MOST_CONSISTENT: String
    val YAHTZEE_STATS_GLOBAL_MOST_SCORED: String
    val YAHTZEE_STATS_GLOBAL_LEAST_SCORED: String
    val YAHTZEE_STATS_GLOBAL_BEST_AVG: String

    // Leaderboards
    val YAHTZEE_STATS_LEADERBOARD_MOST_WINS: String
    val YAHTZEE_STATS_LEADERBOARD_HIGHEST_SCORES: String
    val YAHTZEE_STATS_LEADERBOARD_MOST_YAHTZEES: String

    // Category Heatmap Sections
    val YAHTZEE_STATS_HEATMAP_UPPER: String
    val YAHTZEE_STATS_HEATMAP_LOWER: String

    // Format Strings
    val YAHTZEE_FORMAT_WINS: String
    val YAHTZEE_FORMAT_YAHTZEE_RATE: String
    val YAHTZEE_FORMAT_ACTIVE_PLAYER: String
    val YAHTZEE_FORMAT_HIGH_SCORE: String
    val YAHTZEE_FORMAT_MOST_YAHTZEES: String
    val YAHTZEE_FORMAT_PLAYER_COUNT: String
    val YAHTZEE_FORMAT_LUCKIEST_PLAYER: String
    val YAHTZEE_FORMAT_CATEGORY_AVG: String
    val YAHTZEE_FORMAT_WINNER_SCORE: String

    // Rank Indicators
    val YAHTZEE_RANK_FIRST: String
    val YAHTZEE_RANK_SECOND: String
    val YAHTZEE_RANK_THIRD: String
    val YAHTZEE_RANK_FORMAT: String
    val YAHTZEE_RANK_FIRST_PLACE: String
    val YAHTZEE_RANK_SECOND_PLACE: String
    val YAHTZEE_RANK_THIRD_PLACE: String

    // Error Messages
    val YAHTZEE_ERROR_LOAD_FAILED: String
    val YAHTZEE_ERROR_STATS_FAILED: String
    val YAHTZEE_ERROR_GLOBAL_FAILED: String

    // Player Management - Dialog Titles
    val PLAYER_DIALOG_NEW_TITLE: String
    val PLAYER_DIALOG_EDIT_TITLE: String

    // Player Selector Component
    val PLAYER_SELECTOR_PLACEHOLDER: String
    val PLAYER_SELECTOR_CHANGE: String
    val PLAYER_SELECTOR_SELECT: String
    val PLAYER_SELECTOR_DIALOG_TITLE: String
    val PLAYER_SELECTOR_SEARCH_PLACEHOLDER: String
    val PLAYER_REACTIVATE_FORMAT: String
    val PLAYER_CREATE_FORMAT: String

    // Dice Roller Screen
    val DICE_SCREEN_TITLE: String

    // Color Picker
    val COLOR_PICKER_CD: String
    val COLOR_PICKER_DIALOG_TITLE: String

    // Finger Selector
    val FINGER_SELECTOR_LABEL_FINGERS: String
    val FINGER_SELECTOR_LABEL_GROUPS: String

    // Counter Screen - Player Count Format
    val COUNTER_FORMAT_PLAYER_COUNT: String

    // Error Handling
    val ERROR_UNKNOWN: String

    // Validation Messages
    val ERROR_MIN_FINGERS: String
    val ERROR_PLAYER_COUNT_RANGE: String
    val PLAYERS_COUNT_FORMAT: String
    val PLAYER_COUNT_DISPLAY: String

    // Settings Screen
    val SETTINGS_LANGUAGE: String

    // Helper methods for pluralization
    fun playerCount(count: Int): String
}
