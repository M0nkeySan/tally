package io.github.m0nkeysan.tally.core.domain.model

data class HomeFeatureState(
    val featureId: String,
    val enabled: Boolean,
    val order: Int
)

val defaultFeatureStates = listOf(
    HomeFeatureState("finger_selector", enabled = true, order = 0),
    HomeFeatureState("tarot", enabled = true, order = 1),
    HomeFeatureState("yahtzee", enabled = true, order = 2),
    HomeFeatureState("counter", enabled = true, order = 3),
    HomeFeatureState("dice_roller", enabled = true, order = 4)
)
