package io.github.m0nkeysan.gamekeeper.core.data.local.database

import androidx.room.RoomDatabaseConstructor

/**
 * Room database constructor for multiplatform support.
 * Required for non-Android platforms (iOS).
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object GameDatabaseConstructor : RoomDatabaseConstructor<GameDatabase>
