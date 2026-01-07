package io.github.m0nkeysan.gamekeeper.platform

data class DeviceInfo(
    val platform: String,
    val model: String,
    val screenWidth: Int,
    val screenHeight: Int
)

expect fun getDeviceInfo(): DeviceInfo
