package io.github.m0nkeysan.tally.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import io.github.m0nkeysan.tally.generated.resources.cd_tarot_icon
import io.github.m0nkeysan.tally.generated.resources.cd_yahtzee_icon
import io.github.m0nkeysan.tally.generated.resources.tarot
import io.github.m0nkeysan.tally.generated.resources.yahtzee
import io.github.m0nkeysan.tally.generated.resources.Res

@Composable
fun YahtzeeIcon() {
    Image(
         painter = painterResource(Res.drawable.yahtzee),
         contentDescription = stringResource(Res.string.cd_yahtzee_icon),
         modifier = Modifier.size(64.dp),
         colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
     )
}

@Composable
fun TarotIcon() {
    Image(
         painter = painterResource(Res.drawable.tarot),
         contentDescription = stringResource(Res.string.cd_tarot_icon),
         modifier = Modifier.size(64.dp),
         colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
     )
}