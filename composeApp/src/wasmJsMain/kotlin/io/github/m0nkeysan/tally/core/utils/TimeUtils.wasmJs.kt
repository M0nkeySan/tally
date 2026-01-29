package io.github.m0nkeysan.tally.core.utils

private fun jsDateNow(): Double = js("Date.now()")

actual fun getCurrentTimeMillis(): Long {
    return jsDateNow().toLong()
}
