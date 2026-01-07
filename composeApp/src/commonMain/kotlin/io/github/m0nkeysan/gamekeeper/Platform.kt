package io.github.m0nkeysan.gamekeeper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform