package io.github.m0nkeysan.gamekeeper.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import gamekeeper.composeapp.generated.resources.Res
import gamekeeper.composeapp.generated.resources.tarot
import gamekeeper.composeapp.generated.resources.yahtzee
import io.github.m0nkeysan.gamekeeper.ui.strings.AppStrings
import org.jetbrains.compose.resources.painterResource

@Composable
fun YahtzeeIcon() {
    Image(
         painter = painterResource(Res.drawable.yahtzee),
         contentDescription = AppStrings.CD_YAHTZEE_ICON,
         modifier = Modifier.size(64.dp),
         colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
     )
}

@Composable
fun TarotIcon() {
    Image(
         painter = painterResource(Res.drawable.tarot),
         contentDescription = AppStrings.CD_TAROT_ICON,
         modifier = Modifier.size(64.dp),
         colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
     )
}