package io.github.m0nkeysan.tally.platform

import kotlin.js.JsAny

/**
 * JavaScript console API for logging
 */
external object console {
    fun log(message: String)
    fun warn(message: String)
    fun error(message: String)
}

/**
 * JavaScript Number prototype methods
 */
external interface JsNumber : JsAny {
    fun toFixed(digits: Int): String
}

/**
 * JavaScript window object
 */
external object window {
    val navigator: Navigator
    val location: Location
}

external interface Navigator : JsAny {
    val language: String
}

external interface Location : JsAny {
    val hash: String
    val href: String
}

/**
 * localStorage API
 */
external object localStorage {
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
    fun removeItem(key: String)
    fun clear()
}
