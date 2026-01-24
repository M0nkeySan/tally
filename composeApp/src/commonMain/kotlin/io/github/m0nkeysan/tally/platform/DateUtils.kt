package io.github.m0nkeysan.tally.platform

expect fun getCurrentDateTimeString(): String

expect fun formatTimestamp(timestamp: Long): String