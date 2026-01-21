package io.github.m0nkeysan.gamekeeper

import androidx.compose.ui.window.ComposeUIViewController

/**
 * iOS entry point for the Compose Multiplatform application.
 * This function is called from Swift/SwiftUI to create the UIViewController
 * that hosts the Compose UI.
 */
fun MainViewController() = ComposeUIViewController { App() }
