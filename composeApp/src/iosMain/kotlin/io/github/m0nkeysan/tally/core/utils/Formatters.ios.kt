package io.github.m0nkeysan.tally.core.utils

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun Double.format(digits: Int): String {
    return NSString.stringWithFormat("%.${digits}f", this)
}