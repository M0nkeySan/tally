package io.github.m0nkeysan.gamekeeper

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.m0nkeysan.gamekeeper.core.navigation.GameNavGraph

@Composable
fun App() {
    MaterialTheme {
        GameNavGraph()
    }
}