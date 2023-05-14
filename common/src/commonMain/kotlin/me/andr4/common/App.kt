package me.andr4.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    Column {
        Button(onClick = {
            text = "Hello, ${getPlatformName()}"
        }) {
            Text(text)
        }
        Surface(
            shape = RectangleShape,
            color = Color.Black,
            elevation = 20.dp,
        ){
            Box(modifier = Modifier.size(64.dp).background(Color.Red))
        }

    }

}
