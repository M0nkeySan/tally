package io.github.m0nkeysan.gamekeeper.core.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object FingerSelector : Screen("finger_selector")
    data object Tarot : Screen("tarot")
    data object TarotCreation : Screen("tarot_creation")
    data object TarotScoring : Screen("tarot_scoring/{gameId}") {
        fun createRoute(gameId: String) = "tarot_scoring/$gameId"
    }
    data object TarotRoundAddition : Screen("tarot_round_addition/{gameId}?roundId={roundId}") {
        fun createRoute(gameId: String, roundId: String? = null) = 
            "tarot_round_addition/$gameId" + (roundId?.let { "?roundId=$it" } ?: "")
    }
    data object Yahtzee : Screen("yahtzee")
    data object YahtzeeCreation : Screen("yahtzee_creation")
    data object YahtzeeScoring : Screen("yahtzee_scoring/{gameId}") {
        fun createRoute(gameId: String) = "yahtzee_scoring/$gameId"
    }
    data object YahtzeeSummary : Screen("yahtzee_summary/{gameId}") {
        fun createRoute(gameId: String) = "yahtzee_summary/$gameId"
    }
    data object Counter : Screen("counter")
    data object EditCounter : Screen("edit_counter/{id}?name={name}&count={count}&color={color}") {
        fun createRoute(id: String, name: String, count: Int, color: Long) = 
            "edit_counter/$id?name=${name}&count=$count&color=$color"
    }
     data object History : Screen("history")
     data object Players : Screen("players")
     data object DiceRoller : Screen("dice_roller")
}
