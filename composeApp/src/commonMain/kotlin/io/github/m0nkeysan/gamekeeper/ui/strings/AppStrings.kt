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
     
     // Game Operations
     const val GAME_DELETE_ALL_TITLE = "Delete All Games"
     const val GAME_DELETE_ALL_CONFIRM = "Are you sure you want to delete all games? This cannot be undone."
     const val GAME_CREATE = "Create Game"
     const val GAME_NAME_LABEL = "Game Name"
     const val GAME_LOADING = "Loading games..."
     const val GAME_EMPTY_STATE = "No games yet. Create one!"
     
     // Counter Screen
     const val COUNTER_HISTORY_NOTICE = "History is cleared when you close the app"
     
     // Dice Roller
     const val GAME_DICE = "Dice Roller"
     const val DESC_DICE = "Roll customizable dice for any board game"
     const val DICE_SETTINGS_TITLE = "Dice Settings"
     const val DICE_NUMBER_OF_DICE = "Number of Dice"
     const val DICE_TYPE = "Dice Type"
     const val DICE_ENABLE_ANIMATION = "Enable Animation"
     const val DICE_SHAKE_TO_ROLL = "Shake to Roll"
     const val DICE_CUSTOM_SIDES = "Custom Sides"
     const val DICE_CUSTOM_SIDES_HINT = "Enter number of sides (2-99)"
     const val DICE_TOTAL = "Total"
     const val DICE_TAP_TO_ROLL = "Tap to roll"
     const val DICE_TAP_OR_SHAKE = "Tap or shake to roll"
     const val DICE_ERROR_INVALID = "Please enter a valid number"
     const val DICE_ERROR_MIN = "Minimum is 2 sides"
     const val DICE_ERROR_MAX = "Maximum is 99 sides"
     
      // Common
      const val ACTION_CONFIRM = "Confirm"
      
      // Common Actions (extended)
      const val ACTION_RETRY = "Retry"
      const val ACTION_CLOSE = "CLOSE"
      const val ACTION_OK = "OK"
      const val ACTION_RESET = "Reset"
      
      // Common Content Descriptions (Accessibility)
      const val CD_BACK = "Back"
      const val CD_SETTINGS = "Settings"
      const val CD_ADD = "Add"
      const val CD_DELETE = "Delete"
      const val CD_EDIT = "Edit"
      const val CD_MENU = "Menu"
      const val CD_ADD_PLAYER = "Add Player"
      const val CD_DELETE_ALL = "Delete all"
      const val CD_HISTORY = "History"
      const val CD_NEXT = "Next Player"
      const val CD_PREVIOUS = "Previous Player"
      const val CD_TOGGLE_COLLAPSE = "Collapse"
      const val CD_TOGGLE_EXPAND = "Expand"
      
      // Common Dialogs
      const val DIALOG_DELETE_TITLE = "Delete Game"
      const val DIALOG_DELETE_CONFIRM = "Are you sure you want to delete the game?"
      const val DIALOG_DELETE_ALL_TITLE = "Delete All"
      const val DIALOG_DELETE_ALL_CONFIRM = "Are you sure?"
      const val DIALOG_RESET_TITLE = "Reset All Counters"
      const val DIALOG_RESET_MESSAGE = "Are you sure you want to reset all counter values to 0?"
      const val DIALOG_DELETE_COUNTER_TITLE = "Delete Counter"
      const val DIALOG_DELETE_COUNTER_MESSAGE = "Are you sure you want to delete this counter? This action cannot be undone."
      const val DIALOG_DELETE_EVERYTHING_TITLE = "Delete Everything"
      const val DIALOG_DELETE_EVERYTHING_MESSAGE = "Are you sure you want to delete all counters? This action cannot be undone."
      const val DIALOG_DELETE_COUNTER_HISTORY_TITLE = "Delete all histories"
      const val DIALOG_DELETE_COUNTER_HISTORY_MESSAGE = "Are you sure you want to delete all counter change histories? This cannot be undone."
      const val DIALOG_DEACTIVATE_PLAYER = "Deactivate"
      const val DIALOG_DELETE_PLAYER = "Delete"
      
      // Tarot - Statistics
      const val TAROT_STATS_TAB_CURRENT_GAME = "Current Game"
      const val TAROT_STATS_TAB_PLAYER_STATS = "Player Stats"
      const val TAROT_STATS_LABEL_CALLED = "Called"
      const val TAROT_STATS_LABEL_AS_TAKER = "As Taker"
      const val TAROT_STATS_LABEL_WIN_RATE = "Win Rate"
      const val TAROT_STATS_LABEL_FAVORITE_BID = "Favorite Bid:"
      const val TAROT_STATS_LABEL_MOST_SUCCESSFUL = "Most Successful:"
      const val TAROT_STATS_SECTION_PERFORMANCE_DETAILS = "Performance Details"
      const val TAROT_STATS_LABEL_AVG_BOUTS = "Avg Bouts/Round"
      const val TAROT_STATS_LABEL_AVG_WON = "Avg Won"
      const val TAROT_STATS_LABEL_AVG_LOST = "Avg Lost"
      const val TAROT_STATS_SECTION_CALLED_PERFORMANCE = "Called Performance"
      const val TAROT_STATS_ERROR_TITLE = "Oops! Something went wrong"
      
       // Tarot - Scoring
       const val TAROT_SCORING_SCREEN_TITLE = "Game Scoring"
       const val TAROT_SCORING_CD_BACK = "Back"
       const val TAROT_SCORING_CD_STATISTICS = "Statistics"
       const val TAROT_SCORING_CD_ADD_ROUND = "Add Round"
      const val TAROT_SCORING_SECTION_SCORES = "Scores"
      const val TAROT_SCORING_SECTION_HISTORY = "History"
      const val TAROT_SCORING_EMPTY_ROUNDS = "No rounds yet"
      const val TAROT_SCORING_ROUND_TITLE = "Round %d - %s"
      const val TAROT_SCORING_ROUND_DETAILS = "%s • %d bouts • %d pts"
      const val TAROT_SCORING_ANNOUNCE_PETIT_AU_BOUT = "Petit au bout"
      const val TAROT_SCORING_ANNOUNCE_POIGNEE = "Poignée %s"
      const val TAROT_SCORING_ANNOUNCE_CHELEM = "Chelem: %s"
      const val TAROT_SCORING_GAME_TITLE = "Tarot Games"
      
      // Tarot - Round Addition
      const val TAROT_ROUND_ACTION_SAVE = "SAVE ROUND"
      const val TAROT_ROUND_SECTION_PLAYERS = "PLAYERS"
      const val TAROT_ROUND_LABEL_TAKER = "TAKER"
      const val TAROT_ROUND_LABEL_CALLED_PLAYER = "CALLED PLAYER"
      const val TAROT_ROUND_SECTION_BID = "BID"
      const val TAROT_ROUND_SECTION_BOUTS = "BOUTS"
      const val TAROT_ROUND_LABEL_BOUTS = "%d Bouts"
      const val TAROT_ROUND_SECTION_POINTS = "POINTS"
      const val TAROT_ROUND_FIELD_ATTACKER_SCORE = "Attacker"
      const val TAROT_ROUND_LABEL_DEFENSE = "DEFENSE"
      const val TAROT_ROUND_CONTRACT_WON = "CONTRACT WON (+%d pts)"
      const val TAROT_ROUND_CONTRACT_LOST = "CONTRACT LOST (%d pts)"
      const val TAROT_ROUND_SECTION_ANNOUNCES = "ANNOUNCES"
      const val TAROT_ROUND_ANNOUNCE_PETIT_AU_BOUT = "Petit au bout"
      const val TAROT_ROUND_ANNOUNCE_POIGNEE = "Poignée"
      const val TAROT_ROUND_LABEL_CHELEM = "Chelem"
      
      // Counter - Main Screen
      const val COUNTER_SECTION_LEADER = "%s %s"
      const val COUNTER_CD_BACK = "Back"
      const val COUNTER_CD_ADD = "Add Counter"
      const val COUNTER_CD_HISTORY = "History"
      const val COUNTER_CD_MENU = "Menu"
      const val COUNTER_CD_SETTINGS_MENU = "Settings menu"
      const val COUNTER_CD_RESET_ALL = "Reset all counters"
      const val COUNTER_CD_DELETE_ALL = "Delete all counters"
      const val COUNTER_DIALOG_SETTINGS_TITLE = "Counter Settings"
      const val COUNTER_DIALOG_SETTINGS_LABEL = "Highlight player with:"
      const val COUNTER_SETTINGS_OPTION_MOST = "📈 Most points"
      const val COUNTER_SETTINGS_OPTION_LEAST = "📉 Least points"
      const val COUNTER_ACTION_CLOSE = "CLOSE"
      const val COUNTER_DIALOG_RESET_TITLE = "Reset All Counters"
      const val COUNTER_DIALOG_RESET_MESSAGE = "Are you sure you want to reset all counter values to 0?"
      const val COUNTER_ACTION_RESET = "Reset"
      const val COUNTER_DIALOG_DELETE_TITLE = "Delete Everything"
      const val COUNTER_DIALOG_DELETE_MESSAGE = "Are you sure you want to delete all counters? This action cannot be undone."
      const val COUNTER_ACTION_DELETE = "Delete Everything"
      const val COUNTER_DISPLAY_FORMAT = "%s: %d"
      const val COUNTER_DIALOG_ADJUST_LABEL = "MANUAL ADJUST"
      const val COUNTER_DIALOG_ADJUST_PLACEHOLDER = "0"
      const val COUNTER_DIALOG_SET_SCORE_LABEL = "SET NEW SCORE"
      const val COUNTER_ACTION_CANCEL = "CANCEL"
      const val COUNTER_ACTION_SAVE = "SAVE"
      const val COUNTER_CD_DECREASE = "Decrease"
      const val COUNTER_CD_INCREASE = "Increase"
      
      // Counter - History & Edit
      const val COUNTER_HISTORY_TITLE = "History"
      const val COUNTER_HISTORY_SUBTITLE = "⚠️ History is cleared when you close the app"
      const val COUNTER_HISTORY_CD_BACK = "Back"
      const val COUNTER_HISTORY_CD_DELETE = "Delete all histories"
      const val COUNTER_HISTORY_EMPTY = "No counter changes yet"
      const val COUNTER_HISTORY_DELETED_EMOJI = "🗑️"
      const val COUNTER_HISTORY_DELETED_TEXT = "Deleted"
      const val COUNTER_HISTORY_DELTA_FORMAT = "%+d"
      const val COUNTER_EDIT_TITLE = "Edit Counter"
      const val COUNTER_EDIT_CD_BACK = "Back"
      const val COUNTER_EDIT_CD_DELETE = "Delete"
      const val COUNTER_EDIT_ACTION_SAVE = "SAVE CHANGES"
      const val COUNTER_EDIT_LABEL_COLOR = "ACCENT COLOR"
      const val COUNTER_EDIT_FIELD_NAME = "NAME"
      const val COUNTER_EDIT_PLACEHOLDER_NAME = "Player Name"
      const val COUNTER_EDIT_FIELD_VALUE = "VALUE"
      const val COUNTER_EDIT_PLACEHOLDER_VALUE = "0"
      
      // Yahtzee - Scoring
      const val YAHTZEE_SCORING_CD_PREVIOUS = "Previous Player"
      const val YAHTZEE_SCORING_TURN_INDICATOR = "●"
      const val YAHTZEE_SCORING_FALLBACK_NAME = "Unknown"
      const val YAHTZEE_SCORING_CD_DROPDOWN = "Select player dropdown"
      const val YAHTZEE_SCORING_TOTAL_FORMAT = "Total: %d"
      const val YAHTZEE_SCORING_CD_NEXT = "Next Player"
      const val YAHTZEE_SCORING_TURN_LABEL = "Your Turn"
      const val YAHTZEE_SCORING_VIEWING_LABEL = "Viewing %s's card"
      const val YAHTZEE_SECTION_UPPER = "Upper Section"
      const val YAHTZEE_SECTION_LOWER = "Lower Section"
      const val YAHTZEE_DIALOG_SELECT_SCORE = "Select score for %s"
      const val YAHTZEE_DIALOG_ENTER_SCORE = "Enter score for %s"
      const val YAHTZEE_PLACEHOLDER_DICE_SUM = "Sum of dice"
      const val YAHTZEE_ERROR_SCORE_TOO_HIGH = "Score cannot be higher than 30"
      const val YAHTZEE_LABEL_UPPER_BONUS = "Upper Bonus (63+)"
      const val YAHTZEE_LABEL_BONUS_NEEDED = "-%d"
      const val YAHTZEE_LABEL_BONUS_EARNED = "+35"
      const val YAHTZEE_LABEL_TOTAL_SCORE = "TOTAL SCORE"
      const val YAHTZEE_GAME_TITLE = "Yahtzee Games"
      const val YAHTZEE_NEW_GAME_TITLE = "New Yahtzee Game"
      const val YAHTZEE_GAME_NAME_DEFAULT = "Yahtzee Game"
      
      // Dice Roller
      const val DICE_DISPLAY_FORMAT = "%d × d%d"
      const val DICE_ROLLS_FORMAT = "Rolls: %s"
      const val DICE_TOTAL_FORMAT = "Total: %d"
      const val DICE_INSTRUCTION_TAP = "Tap anywhere to roll. Long-press the box for settings."
      const val DICE_DIALOG_TITLE = "Dice Settings"
      const val DICE_FIELD_NUMBER = "Number of Dice"
      const val DICE_FIELD_TYPE = "Dice Type"
      const val DICE_FIELD_CUSTOM = "Custom Dice"
      const val DICE_ERROR_NOT_VALID = "Must be a valid number"
      const val DICE_ERROR_MIN_SIDES = "Minimum is %d sides"
      const val DICE_ERROR_MAX_SIDES = "Maximum is %d sides"
      const val DICE_PLACEHOLDER_CUSTOM = "2-99"
      const val DICE_SETTING_ANIMATION = "Animation"
      const val DICE_SETTING_SHAKE = "Shake to Roll"
      const val DICE_ACTION_CANCEL = "Cancel"
      const val DICE_ACTION_SAVE = "Save"
      
      // Finger Selector
      const val FINGER_SELECTOR_CD_SETTINGS = "Settings"
      const val FINGER_SELECTOR_DIALOG_TITLE = "Selector Settings"
      const val FINGER_SELECTOR_SECTION_MODE = "Mode"
      const val FINGER_SELECTOR_MODE_FINGERS = "Fingers"
      const val FINGER_SELECTOR_CD_FINGERS = "Fingers filter"
      const val FINGER_SELECTOR_MODE_GROUPS = "Groups"
      const val FINGER_SELECTOR_SLIDER_TITLE = "Number of %s"
      const val FINGER_SELECTOR_SLIDER_FORMAT = "%s: %d"
      const val FINGER_SELECTOR_CD_TOUCH = "Touch here to place finger"
      const val FINGER_SELECTOR_INSTRUCTION_PLACE = "Place your fingers"
      const val FINGER_SELECTOR_INSTRUCTION_WAIT = "Wait for group assignment"
      const val FINGER_SELECTOR_INSTRUCTION_FORMAT = "%d finger%s will be chosen"
      const val FINGER_SELECTOR_ERROR_MIN_FINGERS = "Need at least %d fingers"
      
       // Game Selection & Creation
       const val GAME_SELECTION_ACTION_DELETE_ALL = "Delete All"
       const val GAME_SELECTION_CD_BACK = "Back"
       const val GAME_SELECTION_CD_MENU = "Menu"
       const val GAME_SELECTION_CD_STATISTICS = "Statistics"
       const val GAME_SELECTION_CD_CREATE = "Create new game"
       const val GAME_SELECTION_CD_DELETE_ALL = "Delete all games"
      const val GAME_SELECTION_FAB_CREATE = "Create new game"
      const val GAME_SELECTION_LOADING = "Loading games..."
      const val GAME_SELECTION_EMPTY = "No games yet. Create one!"
      const val GAME_SELECTION_ACTION_CREATE = "Create Game"
      const val GAME_SELECTION_CD_DELETE_GAME = "Delete game"
      const val GAME_CREATION_CD_BACK = "Back"
      const val GAME_CREATION_ACTION_CANCEL = "Cancel"
      const val GAME_CREATION_ACTION_CREATE = "Create Game"
      const val GAME_CREATION_NEW_TAROT_TITLE = "New Tarot Game"
      const val GAME_CREATION_TAROT_NAME_DEFAULT = "Tarot Game"
      const val GAME_CREATION_NEW_YAHTZEE_TITLE = "New Yahtzee Game"
      const val GAME_CREATION_YAHTZEE_NAME_DEFAULT = "Yahtzee Game"
      const val GAME_CREATION_FIELD_GAME_NAME = "Game Name"
      const val GAME_DELETION_DIALOG_TAROT_TITLE = "Delete Game"
      const val GAME_DELETION_DIALOG_TAROT_MESSAGE = "Are you sure you want to delete the game '%s'? This will also delete all rounds in this game."
      const val GAME_DELETION_DIALOG_YAHTZEE_TITLE = "Delete Game"
      const val GAME_DELETION_DIALOG_YAHTZEE_MESSAGE = "Are you sure you want to delete '%s'? All scores will be lost."
      
      // Player Management
      const val PLAYER_SECTION_PLAYERS = "Players"
      const val PLAYER_SECTION_DEACTIVATED = "Deactivated"
      const val PLAYER_DIALOG_TITLE = "Players"
      const val PLAYER_CD_BACK = "Back"
      const val PLAYER_CD_ADD = "Add Player"
      const val PLAYER_FIELD_NAME = "NAME"
      const val PLAYER_PLACEHOLDER_NAME = "Player Name"
      const val PLAYER_ERROR_NAME_TAKEN = "This name is already taken"
      const val PLAYER_LABEL_COLOR = "AVATAR COLOR"
      const val PLAYER_ACTION_CANCEL = "Cancel"
      const val PLAYER_CD_EDIT = "Edit"
      
      // Home & Results
      const val HOME_CD_GAMES = "Games"
      const val HOME_CD_PLAYERS = "Players"
      const val HOME_CD_FAB_ADD = "Add Player"
      const val HOME_CD_FINGER_SELECTOR = "Finger Selector game"
      const val HOME_TITLE_FINGER_SELECTOR = "Finger Selector"
      const val HOME_DESC_FINGER_SELECTOR = "Randomly select a starting player with multi-touch"
      const val HOME_CD_COUNTER = "Counter game"
      const val HOME_CD_DICE = "Dice Roller game"
       const val RESULTS_ACTION_BACK = "BACK TO HOME"
       const val RESULTS_TITLE_TIE = "IT'S A TIE!"
       const val RESULTS_TITLE_WINNER = "WE HAVE A WINNER!"
       const val RESULTS_SECTION_SCORES = "FINAL SCORES"
       
       // Yahtzee - Statistics Screen
       const val YAHTZEE_STATS_TITLE = "Statistics"
       const val YAHTZEE_STATS_GLOBAL = "⭐ Global Statistics"
       const val YAHTZEE_STATS_SELECT_PLAYER = "Select Player"
       const val YAHTZEE_STATS_NO_DATA = "No data available"
       
       // Player Statistics Sections
       const val YAHTZEE_STATS_OVERALL_PERFORMANCE = "📊 Overall Performance"
       const val YAHTZEE_STATS_SCORE_BOX = "📈 Score Box Performance"
       const val YAHTZEE_STATS_RECENT_GAMES = "🎮 Recent Games"
       
       // Player Statistics Labels
       const val YAHTZEE_STATS_TOTAL_GAMES = "Total Games"
       const val YAHTZEE_STATS_FINISHED_GAMES = "Finished Games"
       const val YAHTZEE_STATS_WINS = "Wins"
       const val YAHTZEE_STATS_AVERAGE_SCORE = "Average Score"
       const val YAHTZEE_STATS_PERSONAL_BEST = "Personal Best"
       const val YAHTZEE_STATS_TOTAL_YAHTZEES = "Total Yahtzees"
       const val YAHTZEE_STATS_YAHTZEE_RATE = "Yahtzee Rate"
       const val YAHTZEE_STATS_UPPER_SECTION = "Upper Section"
       const val YAHTZEE_STATS_LOWER_SECTION = "Lower Section"
       const val YAHTZEE_STATS_BONUS_RATE = "Bonus Rate"
       
       // Global Statistics Sections
       const val YAHTZEE_STATS_GLOBAL_OVERALL = "📊 Overall Statistics"
       const val YAHTZEE_STATS_GLOBAL_FUN_FACTS = "🎲 Fun Facts"
       const val YAHTZEE_STATS_GLOBAL_LEADERBOARDS = "🏆 Leaderboards"
       const val YAHTZEE_STATS_GLOBAL_CATEGORY = "📈 Category Performance"
       
       // Global Statistics Labels
       const val YAHTZEE_STATS_GLOBAL_FINISHED = "Finished Games"
       const val YAHTZEE_STATS_GLOBAL_TOTAL_PLAYERS = "Total Players"
       const val YAHTZEE_STATS_GLOBAL_MOST_ACTIVE = "Most Active Player"
       const val YAHTZEE_STATS_GLOBAL_HIGH_SCORE = "All-Time High Score"
       const val YAHTZEE_STATS_GLOBAL_AVERAGE_SCORE = "Average Score"
       const val YAHTZEE_STATS_GLOBAL_TOTAL_YAHTZEES = "Total Yahtzees"
       const val YAHTZEE_STATS_GLOBAL_YAHTZEE_RATE = "Yahtzee Rate"
       const val YAHTZEE_STATS_GLOBAL_MOST_YAHTZEES_GAME = "Most Yahtzees (Single Game)"
       const val YAHTZEE_STATS_GLOBAL_UPPER_BONUS = "Upper Bonus Rate"
       const val YAHTZEE_STATS_GLOBAL_DICE_ROLLS = "Estimated Dice Rolls"
       const val YAHTZEE_STATS_GLOBAL_POINTS_SCORED = "Total Points Scored"
       const val YAHTZEE_STATS_GLOBAL_AVG_PLAYERS = "Avg Players/Game"
       const val YAHTZEE_STATS_GLOBAL_LUCKIEST = "Luckiest Player"
       const val YAHTZEE_STATS_GLOBAL_MOST_CONSISTENT = "Most Consistent"
       const val YAHTZEE_STATS_GLOBAL_MOST_SCORED = "Most Scored Category"
       const val YAHTZEE_STATS_GLOBAL_LEAST_SCORED = "Least Scored Category"
       const val YAHTZEE_STATS_GLOBAL_BEST_AVG = "Best Average Category"
       
       // Leaderboards
       const val YAHTZEE_STATS_LEADERBOARD_MOST_WINS = "Most Wins"
       const val YAHTZEE_STATS_LEADERBOARD_HIGHEST_SCORES = "Highest Scores"
       const val YAHTZEE_STATS_LEADERBOARD_MOST_YAHTZEES = "Most Yahtzees"
       
       // Category Heatmap Sections
       const val YAHTZEE_STATS_HEATMAP_UPPER = "UPPER SECTION"
       const val YAHTZEE_STATS_HEATMAP_LOWER = "LOWER SECTION"
       
       // Format Strings
       const val YAHTZEE_FORMAT_WINS = "%s (%s)"
       const val YAHTZEE_FORMAT_YAHTZEE_RATE = "%s per game"
       const val YAHTZEE_FORMAT_ACTIVE_PLAYER = "%s (%d games)"
       const val YAHTZEE_FORMAT_HIGH_SCORE = "%d by %s"
       const val YAHTZEE_FORMAT_MOST_YAHTZEES = "%d by %s"
       const val YAHTZEE_FORMAT_PLAYER_COUNT = "%d players"
       const val YAHTZEE_FORMAT_LUCKIEST_PLAYER = "%s (%s/game)"
       const val YAHTZEE_FORMAT_CATEGORY_AVG = "%s (%s)"
       const val YAHTZEE_FORMAT_WINNER_SCORE = "Winner: %s"
       
       // Rank Indicators
       const val YAHTZEE_RANK_FIRST = "🥇"
       const val YAHTZEE_RANK_SECOND = "🥈"
       const val YAHTZEE_RANK_THIRD = "🥉"
       const val YAHTZEE_RANK_TROPHY = "🏆"
       const val YAHTZEE_RANK_FORMAT = "#%d"
       const val YAHTZEE_RANK_FIRST_PLACE = "🏆 1st"
       const val YAHTZEE_RANK_SECOND_PLACE = "🥈 2nd"
       const val YAHTZEE_RANK_THIRD_PLACE = "🥉 3rd"
       
       // Error Messages
       const val YAHTZEE_ERROR_LOAD_FAILED = "Failed to load: %s"
       const val YAHTZEE_ERROR_STATS_FAILED = "Failed to load statistics: %s"
       const val YAHTZEE_ERROR_GLOBAL_FAILED = "Failed to load global statistics: %s"
       
       // Player Management - Dialog Titles
       const val PLAYER_DIALOG_NEW_TITLE = "New Player"
       const val PLAYER_DIALOG_EDIT_TITLE = "Edit Player"
       
       // Player Selector Component
       const val PLAYER_SELECTOR_PLACEHOLDER = "Select %s"
       const val PLAYER_SELECTOR_CHANGE = "CHANGE"
       const val PLAYER_SELECTOR_SELECT = "SELECT"
       const val PLAYER_SELECTOR_DIALOG_TITLE = "Choose Player"
       const val PLAYER_SELECTOR_SEARCH_PLACEHOLDER = "Search or add new..."
       const val PLAYER_REACTIVATE_FORMAT = "Reactivate \"%s\""
       const val PLAYER_CREATE_FORMAT = "Create \"%s\""
       
       // Dice Roller Screen
       const val DICE_SCREEN_TITLE = "Dice"
       
       // Color Picker
       const val COLOR_PICKER_CD = "Custom Color"
       const val COLOR_PICKER_DIALOG_TITLE = "Pick a Color"
       
       // Finger Selector
       const val FINGER_SELECTOR_LABEL_FINGERS = "Number of Fingers"
       const val FINGER_SELECTOR_LABEL_GROUPS = "Number of Groups"
       
       // Counter Screen - Player Count Format
       const val COUNTER_FORMAT_PLAYER_COUNT = "%dP"
       
       // Error Handling
       const val ERROR_UNKNOWN = "Unknown"
}
