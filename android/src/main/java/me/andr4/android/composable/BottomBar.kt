package me.andr4.android.composable

import androidx.compose.material.BottomAppBar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.andr4.android.AppState

@Composable
fun BottomBar(appState: AppState) {
    if (!appState.showShowBottomBar()) {return}

    BottomAppBar(
        backgroundColor = Color.White,
        elevation = 10.dp
    ) {
        Text("This is bottom bar")
    }

}