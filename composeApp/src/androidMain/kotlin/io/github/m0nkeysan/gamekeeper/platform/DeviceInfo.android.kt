package io.github.m0nkeysan.gamekeeper.platform

import android.content.res.Resources
import android.os.Build

actual fun getDeviceInfo(): DeviceInfo {
    val displayMetrics = Resources.getSystem().displayMetrics
    return DeviceInfo(
        platform = "Android ${Build.VERSION.SDK_INT}",
        model = "${Build.MANUFACTURER} ${Build.MODEL}",
        screenWidth = displayMetrics.widthPixels,
        screenHeight = displayMetrics.heightPixels
    )
}
