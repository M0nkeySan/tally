package io.github.m0nkeysan.gamekeeper.ui.strings

/**
 * English string resources for the GameKeeper application.
 * Implements [StringProvider] for language-specific string management.
 */
object AppStringsEn : StringProvider {
    // Home Screen
    override val HOME_TITLE = "GameKeeper"

    // Counter Screen
    override val COUNTER_TITLE = "Counter"
    override val COUNTER_SETTINGS = "Settings"
    override val COUNTER_REINITIALIZE_ALL = "Reinitialise all"
    override val COUNTER_DELETE_EVERYTHING = "Delete everything"
    override val COUNTER_EMPTY_STATE = "Tap + to add a counter"
    override val COUNTER_REMOVE_LABEL = "REMOVE (-)"
    override val COUNTER_ADD_LABEL = "ADD (+)"

    // Player Selection Screen
    override val PLAYERS_NO_PLAYERS = "No players found. Add one!"

    // Common Actions
    override val ACTION_DELETE = "Delete"
    override val ACTION_DELETE_ALL = "Delete All"
    override val ACTION_CANCEL = "Cancel"
    override val ACTION_SAVE = "Save"
    override val ACTION_BACK = "Back"
    override val ACTION_CREATE = "Create"
    override val ACTION_RESET = "Reset"

    // Loading and States
    override val STATE_LOADING = "Loading..."
    override val STATE_LOADING_GAMES = "Loading games..."

    // Game Specific
    override val GAME_TAROT = "Tarot"
    override val GAME_YAHTZEE = "Yahtzee"
    override val GAME_FINGER_SELECTOR = "Finger Selector"
    override val GAME_COUNTER = "Counter"

    // Descriptions
    override val DESC_TAROT = "Score Tarot games for 3, 4, or 5 players"
    override val DESC_YAHTZEE = "Complete Yahtzee scorecard with automatic bonuses"
    override val DESC_COUNTER = "Simple counter for any board game"

    // Game Operations
    override val GAME_DELETE_ALL_TITLE = "Delete All Games"
    override val GAME_DELETE_ALL_CONFIRM =
        "Are you sure you want to delete all games? This cannot be undone."
    override val GAME_CREATE = "Create Game"

    // Dice Roller
    override val GAME_DICE = "Dice Roller"
    override val DESC_DICE = "Roll customizable dice for any board game"

    // Common Actions (extended)
    override val ACTION_RETRY = "Retry"
    override val ACTION_OK = "OK"

    // Common Content Descriptions (Accessibility)
    override val CD_BACK = "Back"
    override val CD_SETTINGS = "Settings"
    override val CD_ADD = "Add"
    override val CD_MENU = "Menu"
    override val CD_ADD_PLAYER = "Add Player"
    override val CD_TOGGLE_COLLAPSE = "Collapse"
    override val CD_TOGGLE_EXPAND = "Expand"
    override val CD_PLAYER = "Player avatar"
    override val CD_GAME_ICON = "Game icon"
    override val CD_YAHTZEE_ICON = "Yahtzee"
    override val CD_TAROT_ICON = "Tarot"
    override val CD_FINISHED_GAME = "Finished game"
    override val CD_REMOVE_PLAYER = "Remove player"

    // Common Dialogs
    override val DIALOG_DELETE_ALL_TITLE = "Delete All"
    override val DIALOG_DELETE_COUNTER_TITLE = "Delete Counter"
    override val DIALOG_DELETE_COUNTER_MESSAGE =
        "Are you sure you want to delete this counter? This action cannot be undone."
    override val DIALOG_DEACTIVATE_PLAYER = "Deactivate"
    override val DIALOG_DELETE_PLAYER = "Delete"

    // Tarot - Statistics
    override val TAROT_STATS_TAB_CURRENT_GAME = "Current Game"
    override val TAROT_STATS_TAB_PLAYER_STATS = "Player Stats"
    override val TAROT_STATS_LABEL_CALLED = "Called"
    override val TAROT_STATS_LABEL_AS_TAKER = "As Taker"
    override val TAROT_STATS_LABEL_WIN_RATE = "Win Rate"
    override val TAROT_STATS_LABEL_FAVORITE_BID = "Favorite Bid:"
    override val TAROT_STATS_LABEL_MOST_SUCCESSFUL = "Most Successful:"
    override val TAROT_STATS_SECTION_PERFORMANCE_DETAILS = "Performance Details"
    override val TAROT_STATS_LABEL_AVG_BOUTS = "Avg Bouts/Round"
    override val TAROT_STATS_LABEL_AVG_WON = "Avg Won"
    override val TAROT_STATS_LABEL_AVG_LOST = "Avg Lost"
    override val TAROT_STATS_SECTION_CALLED_PERFORMANCE = "Called Performance"
    override val TAROT_STATS_ERROR_TITLE = "Oops! Something went wrong"

    // Tarot - Scoring
    override val TAROT_SCORING_SCREEN_TITLE = "Game Scoring"
    override val TAROT_SCORING_CD_ADD_ROUND = "Add Round"
    override val TAROT_SCORING_SECTION_SCORES = "Scores"
    override val TAROT_SCORING_SECTION_HISTORY = "History"
    override val TAROT_SCORING_EMPTY_ROUNDS = "No rounds yet"
    override val TAROT_SCORING_ROUND_TITLE = "Round %d - %s"
    override val TAROT_SCORING_ROUND_DETAILS = "%s • %d bouts • %d pts"
    override val TAROT_SCORING_ANNOUNCE_PETIT_AU_BOUT = "Petit au bout"
    override val TAROT_SCORING_ANNOUNCE_POIGNEE = "Poignée %s"
    override val TAROT_SCORING_ANNOUNCE_CHELEM = "Chelem: %s"
    override val TAROT_SCORING_GAME_TITLE = "Tarot Games"

    // Tarot - Round Addition
    override val TAROT_ROUND_ACTION_SAVE = "SAVE ROUND"
    override val TAROT_ROUND_SECTION_PLAYERS = "PLAYERS"
    override val TAROT_ROUND_LABEL_TAKER = "TAKER"
    override val TAROT_ROUND_LABEL_CALLED_PLAYER = "CALLED PLAYER"
    override val TAROT_ROUND_SECTION_BID = "BID"
    override val TAROT_ROUND_SECTION_BOUTS = "BOUTS"
    override val TAROT_ROUND_LABEL_BOUTS = "%d Bouts"
    override val TAROT_ROUND_SECTION_POINTS = "POINTS"
    override val TAROT_ROUND_FIELD_ATTACKER_SCORE = "Attacker"
    override val TAROT_ROUND_LABEL_DEFENSE = "DEFENSE"
    override val TAROT_ROUND_CONTRACT_WON = "CONTRACT WON (+%d pts)"
    override val TAROT_ROUND_CONTRACT_LOST = "CONTRACT LOST (%d pts)"
    override val TAROT_ROUND_SECTION_ANNOUNCES = "ANNOUNCES"
    override val TAROT_ROUND_ANNOUNCE_PETIT_AU_BOUT = "Petit au bout"
    override val TAROT_ROUND_ANNOUNCE_POIGNEE = "Poignée"
    override val TAROT_ROUND_LABEL_CHELEM = "Chelem"

    // Counter - Main Screen
    override val COUNTER_CD_ADD = "Add Counter"
    override val COUNTER_CD_HISTORY = "History"
    override val COUNTER_CD_RESET_ALL = "Reset all counters"
    override val COUNTER_CD_DELETE_ALL = "Delete all counters"
    override val COUNTER_DIALOG_SETTINGS_TITLE = "Counter Settings"
    override val COUNTER_DIALOG_SETTINGS_LABEL = "Highlight player with:"
    override val COUNTER_SETTINGS_OPTION_MOST = "📈 Most points"
    override val COUNTER_SETTINGS_OPTION_LEAST = "📉 Least points"
    override val COUNTER_ACTION_CLOSE = "CLOSE"
    override val COUNTER_DIALOG_RESET_TITLE = "Reset All Counters"
    override val COUNTER_DIALOG_RESET_MESSAGE =
        "Are you sure you want to reset all counter values to 0?"
    override val COUNTER_DIALOG_DELETE_TITLE = "Delete Everything"
    override val COUNTER_DIALOG_DELETE_MESSAGE =
        "Are you sure you want to delete all counters? This action cannot be undone."
    override val COUNTER_ACTION_DELETE = "Delete Everything"
    override val COUNTER_DIALOG_ADJUST_LABEL = "MANUAL ADJUST"
    override val COUNTER_DIALOG_ADJUST_PLACEHOLDER = "0"
    override val COUNTER_DIALOG_SET_SCORE_LABEL = "SET NEW SCORE"
    override val COUNTER_CD_DECREASE = "Decrease"
    override val COUNTER_CD_INCREASE = "Increase"
    override val COUNTER_LEADER_DISPLAY_FORMAT = "%s %s"

    // Counter - History & Edit
    override val COUNTER_HISTORY_TITLE = "History"
    override val COUNTER_HISTORY_SUBTITLE = "⚠️ History is cleared when you close the app"
    override val COUNTER_HISTORY_CD_DELETE = "Delete all histories"
    override val COUNTER_HISTORY_EMPTY = "No counter changes yet"
    override val COUNTER_HISTORY_DELETED_EMOJI = "🗑️"
    override val COUNTER_HISTORY_DELETED_TEXT = "Deleted"
    override val COUNTER_EDIT_TITLE = "Edit Counter"
    override val COUNTER_EDIT_CD_DELETE = "Delete"
    override val COUNTER_EDIT_ACTION_SAVE = "SAVE CHANGES"
    override val COUNTER_EDIT_LABEL_COLOR = "ACCENT COLOR"
    override val COUNTER_EDIT_FIELD_NAME = "NAME"
    override val COUNTER_EDIT_PLACEHOLDER_NAME = "Player Name"
    override val COUNTER_EDIT_FIELD_VALUE = "VALUE"
    override val COUNTER_EDIT_PLACEHOLDER_VALUE = "0"

    // Yahtzee - Scoring
    override val YAHTZEE_SCORING_CD_PREVIOUS = "Previous Player"
    override val YAHTZEE_SCORING_TURN_INDICATOR = "●"
    override val YAHTZEE_SCORING_FALLBACK_NAME = "Unknown"
    override val YAHTZEE_SCORING_CD_DROPDOWN = "Select player dropdown"
    override val YAHTZEE_SCORING_TOTAL_FORMAT = "Total: %d"
    override val YAHTZEE_SCORING_CD_NEXT = "Next Player"
    override val YAHTZEE_SCORING_TURN_LABEL = "Your Turn"
    override val YAHTZEE_SCORING_VIEWING_LABEL = "Viewing %s's card"
    override val YAHTZEE_SECTION_UPPER = "Upper Section"
    override val YAHTZEE_SECTION_LOWER = "Lower Section"
    override val YAHTZEE_DIALOG_SELECT_SCORE = "Select score for %s"
    override val YAHTZEE_DIALOG_ENTER_SCORE = "Enter score for %s"
    override val YAHTZEE_PLACEHOLDER_DICE_SUM = "Sum of dice"
    override val YAHTZEE_ERROR_SCORE_TOO_HIGH = "Score cannot be higher than 30"
    override val YAHTZEE_LABEL_UPPER_BONUS = "Upper Bonus (63+)"
    override val YAHTZEE_LABEL_BONUS_NEEDED = "-%d"
    override val YAHTZEE_LABEL_BONUS_EARNED = "+35"
    override val YAHTZEE_LABEL_TOTAL_SCORE = "TOTAL SCORE"
    override val YAHTZEE_GAME_TITLE = "Yahtzee Games"
    override val YAHTZEE_NEW_GAME_TITLE = "New Yahtzee Game"
    override val YAHTZEE_GAME_NAME_DEFAULT = "Yahtzee Game"

    // Dice Roller
    override val DICE_DISPLAY_FORMAT = "%d × d%d"
    override val DICE_ROLLS_FORMAT = "Rolls: %s"
    override val DICE_TOTAL_FORMAT = "Total: %d"
    override val DICE_INSTRUCTION_TAP = "Tap anywhere to roll. Long-press the box for settings."
    override val DICE_DIALOG_TITLE = "Dice Settings"
    override val DICE_FIELD_NUMBER = "Number of Dice"
    override val DICE_FIELD_TYPE = "Dice Type"
    override val DICE_FIELD_CUSTOM = "Custom Dice"
    override val DICE_ERROR_NOT_VALID = "Must be a valid number"
    override val DICE_ERROR_MIN_SIDES = "Minimum is %d sides"
    override val DICE_ERROR_MAX_SIDES = "Maximum is %d sides"
    override val DICE_PLACEHOLDER_CUSTOM = "2-99"
    override val DICE_SETTING_ANIMATION = "Animation"
    override val DICE_SETTING_SHAKE = "Shake to Roll"

    // Finger Selector
    override val FINGER_SELECTOR_DIALOG_TITLE = "Selector Settings"
    override val FINGER_SELECTOR_SECTION_MODE = "Mode"
    override val FINGER_SELECTOR_MODE_FINGERS = "Fingers"
    override val FINGER_SELECTOR_MODE_GROUPS = "Groups"
    override val FINGER_SELECTOR_CD_TOUCH = "Touch here to place finger"
    override val FINGER_SELECTOR_INSTRUCTION_PLACE = "Place your fingers"
    override val FINGER_SELECTOR_INSTRUCTION_WAIT = "Wait for group assignment"
    override val FINGER_SELECTOR_SLIDER_VALUE_FORMAT = "%s: %d"

    // Game Selection & Creation
    override val GAME_SELECTION_CD_STATISTICS = "Statistics"
    override val GAME_SELECTION_CD_CREATE = "Create new game"
    override val GAME_SELECTION_CD_DELETE_ALL = "Delete all games"
    override val GAME_SELECTION_LOADING = "Loading games..."
    override val GAME_SELECTION_EMPTY = "No games yet. Create one!"
    override val GAME_SELECTION_CD_DELETE_GAME = "Delete game"
    override val GAME_CREATION_ACTION_CANCEL = "Cancel"
    override val GAME_CREATION_ACTION_CREATE = "Create Game"
    override val GAME_CREATION_NEW_TAROT_TITLE = "New Tarot Game"
    override val GAME_CREATION_TAROT_NAME_DEFAULT = "Tarot Game"
    override val GAME_CREATION_FIELD_GAME_NAME = "Game Name"
    override val GAME_DELETION_DIALOG_TAROT_TITLE = "Delete Game"
    override val GAME_DELETION_DIALOG_TAROT_MESSAGE =
        "Are you sure you want to delete the game '%s'? This will also delete all rounds in this game."
    override val GAME_DELETION_DIALOG_YAHTZEE_TITLE = "Delete Game"
    override val GAME_DELETION_DIALOG_YAHTZEE_MESSAGE =
        "Are you sure you want to delete '%s'? All scores will be lost."

    // Player Management
    override val PLAYER_SECTION_PLAYERS = "Players"
    override val PLAYER_SECTION_DEACTIVATED = "Deactivated"
    override val PLAYER_FIELD_NAME = "NAME"
    override val PLAYER_PLACEHOLDER_NAME = "Player Name"
    override val PLAYER_ERROR_NAME_TAKEN = "This name is already taken"
    override val PLAYER_LABEL_COLOR = "AVATAR COLOR"
    override val PLAYER_CD_EDIT = "Edit"

    // Home & Results
    override val HOME_CD_GAMES = "Games"
    override val HOME_CD_PLAYERS = "Players"
    override val HOME_CD_FINGER_SELECTOR = "Finger Selector game"
    override val HOME_TITLE_FINGER_SELECTOR = "Finger Selector"
    override val HOME_DESC_FINGER_SELECTOR = "Randomly select a starting player with multi-touch"
    override val HOME_CD_COUNTER = "Counter game"
    override val HOME_CD_DICE = "Dice Roller game"
    override val RESULTS_ACTION_BACK = "BACK TO HOME"
    override val RESULTS_TITLE_TIE = "IT'S A TIE!"
    override val RESULTS_TITLE_WINNER = "WE HAVE A WINNER!"
    override val RESULTS_SECTION_SCORES = "FINAL SCORES"

    // Yahtzee - Statistics Screen
    override val YAHTZEE_STATS_GLOBAL = "⭐ Global Statistics"
    override val YAHTZEE_STATS_SELECT_PLAYER = "Select Player"
    override val YAHTZEE_STATS_NO_DATA = "No data available"

    // Player Statistics Sections
    override val YAHTZEE_STATS_OVERALL_PERFORMANCE = "📊 Overall Performance"
    override val YAHTZEE_STATS_SCORE_BOX = "📈 Score Box Performance"
    override val YAHTZEE_STATS_RECENT_GAMES = "🎮 Recent Games"

    // Player Statistics Labels
    override val YAHTZEE_STATS_TOTAL_GAMES = "Total Games"
    override val YAHTZEE_STATS_FINISHED_GAMES = "Finished Games"
    override val YAHTZEE_STATS_WINS = "Wins"
    override val YAHTZEE_STATS_AVERAGE_SCORE = "Average Score"
    override val YAHTZEE_STATS_PERSONAL_BEST = "Personal Best"
    override val YAHTZEE_STATS_TOTAL_YAHTZEES = "Total Yahtzees"
    override val YAHTZEE_STATS_YAHTZEE_RATE = "Yahtzee Rate"
    override val YAHTZEE_STATS_UPPER_SECTION = "Upper Section"
    override val YAHTZEE_STATS_LOWER_SECTION = "Lower Section"
    override val YAHTZEE_STATS_BONUS_RATE = "Bonus Rate"

    // Global Statistics Sections
    override val YAHTZEE_STATS_GLOBAL_OVERALL = "📊 Overall Statistics"
    override val YAHTZEE_STATS_GLOBAL_FUN_FACTS = "🎲 Fun Facts"
    override val YAHTZEE_STATS_GLOBAL_LEADERBOARDS = "🏆 Leaderboards"
    override val YAHTZEE_STATS_GLOBAL_CATEGORY = "📈 Category Performance"

    // Global Statistics Labels
    override val YAHTZEE_STATS_GLOBAL_FINISHED = "Finished Games"
    override val YAHTZEE_STATS_GLOBAL_TOTAL_PLAYERS = "Total Players"
    override val YAHTZEE_STATS_GLOBAL_MOST_ACTIVE = "Most Active Player"
    override val YAHTZEE_STATS_GLOBAL_HIGH_SCORE = "All-Time High Score"
    override val YAHTZEE_STATS_GLOBAL_AVERAGE_SCORE = "Average Score"
    override val YAHTZEE_STATS_GLOBAL_TOTAL_YAHTZEES = "Total Yahtzees"
    override val YAHTZEE_STATS_GLOBAL_YAHTZEE_RATE = "Yahtzee Rate"
    override val YAHTZEE_STATS_GLOBAL_MOST_YAHTZEES_GAME = "Most Yahtzees (Single Game)"
    override val YAHTZEE_STATS_GLOBAL_UPPER_BONUS = "Upper Bonus Rate"
    override val YAHTZEE_STATS_GLOBAL_DICE_ROLLS = "Estimated Dice Rolls"
    override val YAHTZEE_STATS_GLOBAL_POINTS_SCORED = "Total Points Scored"
    override val YAHTZEE_STATS_GLOBAL_AVG_PLAYERS = "Avg Players/Game"
    override val YAHTZEE_STATS_GLOBAL_LUCKIEST = "Luckiest Player"
    override val YAHTZEE_STATS_GLOBAL_MOST_CONSISTENT = "Most Consistent"
    override val YAHTZEE_STATS_GLOBAL_MOST_SCORED = "Most Scored Category"
    override val YAHTZEE_STATS_GLOBAL_LEAST_SCORED = "Least Scored Category"
    override val YAHTZEE_STATS_GLOBAL_BEST_AVG = "Best Average Category"

    // Leaderboards
    override val YAHTZEE_STATS_LEADERBOARD_MOST_WINS = "Most Wins"
    override val YAHTZEE_STATS_LEADERBOARD_HIGHEST_SCORES = "Highest Scores"
    override val YAHTZEE_STATS_LEADERBOARD_MOST_YAHTZEES = "Most Yahtzees"

    // Category Heatmap Sections
    override val YAHTZEE_STATS_HEATMAP_UPPER = "UPPER SECTION"
    override val YAHTZEE_STATS_HEATMAP_LOWER = "LOWER SECTION"

    // Format Strings
    override val YAHTZEE_FORMAT_WINS = "%s (%s)"
    override val YAHTZEE_FORMAT_YAHTZEE_RATE = "%s per game"
    override val YAHTZEE_FORMAT_ACTIVE_PLAYER = "%s (%d games)"
    override val YAHTZEE_FORMAT_HIGH_SCORE = "%d by %s"
    override val YAHTZEE_FORMAT_MOST_YAHTZEES = "%d by %s"
    override val YAHTZEE_FORMAT_PLAYER_COUNT = "%d players"
    override val YAHTZEE_FORMAT_LUCKIEST_PLAYER = "%s (%s/game)"
    override val YAHTZEE_FORMAT_CATEGORY_AVG = "%s (%s)"
    override val YAHTZEE_FORMAT_WINNER_SCORE = "Winner: %s"

    // Rank Indicators
    override val YAHTZEE_RANK_FIRST = "🥇"
    override val YAHTZEE_RANK_SECOND = "🥈"
    override val YAHTZEE_RANK_THIRD = "🥉"
    override val YAHTZEE_RANK_FORMAT = "#%d"
    override val YAHTZEE_RANK_FIRST_PLACE = "🏆 1st"
    override val YAHTZEE_RANK_SECOND_PLACE = "🥈 2nd"
    override val YAHTZEE_RANK_THIRD_PLACE = "🥉 3rd"

    // Error Messages
    override val YAHTZEE_ERROR_LOAD_FAILED = "Failed to load: %s"
    override val YAHTZEE_ERROR_STATS_FAILED = "Failed to load statistics: %s"
    override val YAHTZEE_ERROR_GLOBAL_FAILED = "Failed to load global statistics: %s"

    // Player Management - Dialog Titles
    override val PLAYER_DIALOG_NEW_TITLE = "New Player"
    override val PLAYER_DIALOG_EDIT_TITLE = "Edit Player"

    // Player Selector Component
    override val PLAYER_SELECTOR_PLACEHOLDER = "Select %s"
    override val PLAYER_SELECTOR_CHANGE = "CHANGE"
    override val PLAYER_SELECTOR_SELECT = "SELECT"
    override val PLAYER_SELECTOR_DIALOG_TITLE = "Choose Player"
    override val PLAYER_SELECTOR_SEARCH_PLACEHOLDER = "Search or add new..."
    override val PLAYER_REACTIVATE_FORMAT = "Reactivate \"%s\""
    override val PLAYER_CREATE_FORMAT = "Create \"%s\""

    // Dice Roller Screen
    override val DICE_SCREEN_TITLE = "Dice"

    // Color Picker
    override val COLOR_PICKER_CD = "Custom Color"
    override val COLOR_PICKER_DIALOG_TITLE = "Pick a Color"

    // Finger Selector
    override val FINGER_SELECTOR_LABEL_FINGERS = "Number of Fingers"
    override val FINGER_SELECTOR_LABEL_GROUPS = "Number of Groups"

    // Counter Screen - Player Count Format
    override val COUNTER_FORMAT_PLAYER_COUNT = "%dP"

    // Error Handling
    override val ERROR_UNKNOWN = "Unknown"

    // Validation Messages
    override val ERROR_MIN_FINGERS = "Need at least %d fingers"
    override val ERROR_PLAYER_COUNT_RANGE = "Player count must be between %d and %d"
    override val PLAYERS_COUNT_FORMAT = "Players (%d/%d)"
    override val PLAYER_COUNT_DISPLAY = "%d player%s"

    // Settings Screen
    override val SETTINGS_LANGUAGE = "Language"

    // Helper methods
    override fun playerCount(count: Int): String =
        "$count player${if (count > 1) "s" else ""}"
}
