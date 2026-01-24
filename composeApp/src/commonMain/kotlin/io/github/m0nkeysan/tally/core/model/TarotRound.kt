package io.github.m0nkeysan.tally.core.model

import androidx.compose.runtime.Composable
import io.github.m0nkeysan.tally.generated.resources.Res
import io.github.m0nkeysan.tally.generated.resources.chelem_announced_fail
import io.github.m0nkeysan.tally.generated.resources.chelem_announced_success
import io.github.m0nkeysan.tally.generated.resources.chelem_non_announced_success
import io.github.m0nkeysan.tally.generated.resources.chelem_none
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

import kotlin.uuid.Uuid

@Serializable
data class TarotRound(
    val id: String = Uuid.random().toString(),
    val roundNumber: Int,
    val takerPlayerId: String,
    val bid: TarotBid,
    val bouts: Int,
    val pointsScored: Int,
    val hasPetitAuBout: Boolean,
    val hasPoignee: Boolean,
    val poigneeLevel: PoigneeLevel?,
    val chelem: ChelemType,
    val calledPlayerId: String?,
    val score: Int
)

@Serializable
enum class TarotBid(val displayName: String, val multiplier: Int) {
    PRISE("Prise", 1),
    GARDE("Garde", 2),
    GARDE_SANS("Garde Sans", 4),
    GARDE_CONTRE("Garde Contre", 6)
}

@Serializable
enum class PoigneeLevel(val displayName: String, val bonus: Int) {
    SIMPLE("Simple", 20),
    DOUBLE("Double", 30),
    TRIPLE("Triple", 40)
}

@Serializable
enum class ChelemType(val titleRes: StringResource, val bonus: Int) {
    NONE(
        titleRes = Res.string.chelem_none,
        bonus = 0
    ),
    ANNOUNCED_SUCCESS(
        titleRes = Res.string.chelem_announced_success,
        bonus = 400
    ),
    ANNOUNCED_FAIL(
        titleRes = Res.string.chelem_announced_fail,
        bonus = -200
    ),
    NON_ANNOUNCED_SUCCESS(
        titleRes = Res.string.chelem_non_announced_success,
        bonus = 200
    )
}

@Composable
fun ChelemType.localizedName(): String {
    return stringResource(this.titleRes)
}
