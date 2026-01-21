package io.github.m0nkeysan.gamekeeper.core.utils

actual fun Double.format(digits: Int): String {
    return "%.${digits}f".format(this)
}