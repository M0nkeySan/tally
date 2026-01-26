package io.github.m0nkeysan.tally.core.utils

actual fun getCurrentTimeMillis(): Long {
    return js("Date.now()").unsafeCast<Double>().toLong()
}
