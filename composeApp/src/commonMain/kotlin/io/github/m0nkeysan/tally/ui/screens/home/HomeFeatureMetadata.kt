package io.github.m0nkeysan.tally.ui.screens.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.m0nkeysan.tally.GameIcons
import io.github.m0nkeysan.tally.core.navigation.*
import io.github.m0nkeysan.tally.generated.resources.*
import io.github.m0nkeysan.tally.ui.components.TarotIcon
import io.github.m0nkeysan.tally.ui.components.YahtzeeIcon
import org.jetbrains.compose.resources.stringResource

/**
 * Color scheme for game cards in light and dark themes
 */
data class GameColors(
    val borderLight: Color,
    val backgroundLight: Color,
    val borderDark: Color,
    val backgroundDark: Color
)

/**
 * Predefined color schemes for each game card
 */
private object GameCardColors {
    val fingerSelector = GameColors(
        borderLight = Color(0xFF9333EA),      // Purple-600
        backgroundLight = Color(0xFFF3E8FF),  // Purple-100
        borderDark = Color(0xFF9333EA),       // Purple-600
        backgroundDark = Color(0xFF581C87)    // Purple-900
    )
    
    val tarot = GameColors(
        borderLight = Color(0xFF4F46E5),      // Indigo-600
        backgroundLight = Color(0xFFE0E7FF),  // Indigo-100
        borderDark = Color(0xFF6366F1),       // Indigo-500 (brighter)
        backgroundDark = Color(0xFF312E81)    // Indigo-900
    )
    
    val yahtzee = GameColors(
        borderLight = Color(0xFFDC2626),      // Red-600
        backgroundLight = Color(0xFFFEE2E2),  // Red-100
        borderDark = Color(0xFFEF4444),       // Red-500
        backgroundDark = Color(0xFF7F1D1D)    // Red-900
    )
    
    val counter = GameColors(
        borderLight = Color(0xFF059669),      // Emerald-600
        backgroundLight = Color(0xFFD1FAE5),  // Emerald-100
        borderDark = Color(0xFF10B981),       // Emerald-500
        backgroundDark = Color(0xFF064E3B)    // Emerald-900
    )
    
    val diceRoller = GameColors(
        borderLight = Color(0xFFF59E0B),      // Amber-500
        backgroundLight = Color(0xFFFEF3C7),  // Amber-100
        borderDark = Color(0xFFFCD34D),       // Amber-400 (brighter)
        backgroundDark = Color(0xFF78350F)    // Amber-900
    )
    
    val gameTracker = GameColors(
        borderLight = Color(0xFF0891B2),      // Cyan-600
        backgroundLight = Color(0xFFCFFAFE),  // Cyan-100
        borderDark = Color(0xFF06B6D4),       // Cyan-500
        backgroundDark = Color(0xFF164E63)    // Cyan-900
    )
}

data class GameFeature(
    val id: String,
    val icon: @Composable () -> Unit,
    val title: String,
    val description: String,
    val route: Route,
    val colors: GameColors
)

@Composable
fun getGameFeatureMap() = mapOf(
    "finger_selector" to GameFeature(
        id = "finger_selector",
        icon = {
            Icon(
                GameIcons.TouchApp,
                contentDescription = stringResource(Res.string.home_cd_finger_selector),
                Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
        },
        title = stringResource(Res.string.home_title_finger_selector),
        description = stringResource(Res.string.desc_finger_selector),
        route = FingerSelectorRoute,
        colors = GameCardColors.fingerSelector
    ),
    "tarot" to GameFeature(
        id = "tarot",
        icon = { TarotIcon() },
        title = stringResource(Res.string.game_tarot),
        description = stringResource(Res.string.desc_tarot),
        route = TarotRoute,
        colors = GameCardColors.tarot
    ),
    "yahtzee" to GameFeature(
        id = "yahtzee",
        icon = { YahtzeeIcon() },
        title = stringResource(Res.string.game_yahtzee),
        description = stringResource(Res.string.desc_yahtzee),
        route = YahtzeeRoute,
        colors = GameCardColors.yahtzee
    ),
    "counter" to GameFeature(
        id = "counter",
        icon = {
            Icon(
                GameIcons.AddBox,
                contentDescription = stringResource(Res.string.home_cd_counter),
                Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
        },
        title = stringResource(Res.string.game_counter),
        description = stringResource(Res.string.desc_counter),
        route = CounterRoute,
        colors = GameCardColors.counter
    ),
    "dice_roller" to GameFeature(
        id = "dice_roller",
        icon = {
            Icon(
                GameIcons.Casino,
                contentDescription = stringResource(Res.string.home_cd_dice),
                Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
        },
        title = stringResource(Res.string.game_dice),
        description = stringResource(Res.string.desc_dice),
        route = DiceRollerRoute,
        colors = GameCardColors.diceRoller
    ),
    "game_tracker" to GameFeature(
        id = "game_tracker",
        icon = {
            Icon(
                GameIcons.Scoreboard,
                contentDescription = stringResource(Res.string.home_cd_game_tracker),
                Modifier
                    .width(64.dp)
                    .height(64.dp)
            )
        },
        title = stringResource(Res.string.game_tracker),
        description = stringResource(Res.string.desc_game_tracker),
        route = GameTrackerRoute,
        colors = GameCardColors.gameTracker
    )
)
