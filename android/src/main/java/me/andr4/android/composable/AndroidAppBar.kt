package me.andr4.android.composable

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.andr4.android.AppState

@Composable
fun AndroidAppBar(appState: AppState, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Brodery",
                        color = MaterialTheme.colors.primary,
                        fontSize = MaterialTheme.typography.h5.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                },
                elevation = 10.dp,
                backgroundColor = Color.White
            )
        },
        bottomBar = { BottomBar(appState) },
        floatingActionButton = {
            newProjectActionButton(appState)
        }
    ) {
        content()
    }
}