package io.github.m0nkeysan.tally.core.utils

private fun formatDoubleToFixed(value: Double, digits: Int): String =
    js("value.toFixed(digits)")

actual fun Double.format(digits: Int): String {
    return formatDoubleToFixed(this, digits)
}
