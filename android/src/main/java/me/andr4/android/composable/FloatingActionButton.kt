package me.andr4.android.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.andr4.android.AppState

@Composable
fun newProjectActionButton(appState: AppState) {
    if (!appState.showActionButton()) return

    ExtendedFloatingActionButton(
        onClick = {
            appState.navigateToCreateProject()
                  },
        icon = {
            Icon(
                Icons.Filled.Add,
                contentDescription = "New",
                modifier = Modifier.size(32.dp),
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        text = {
            Text("New")
        }
    )
}