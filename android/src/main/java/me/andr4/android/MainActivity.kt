package me.andr4.android

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import me.andr4.android.composable.AndroidAppBar
import me.andr4.android.composable.BottomBar
import me.andr4.android.composable.editProjectScreen

import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    //lateinit var appState: AppState

    val launcher = Launcher(this)
    val projects = Projects(this)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            projects.loadBitmaps()
            val appState = rememberAppState(projects)

            MaterialTheme {
                AndroidAppBar(appState) {
                    Navigation(appState)
                    //editProjectScreen(appState, projects.projects[0])
                    /*
                    Column {
                        projectGrid(project.projects)
                        //ImageGridLayout(remember { appState.bitmapImageList})
                    }

                     */
                }

                /*
                Row {
                    Button(
                        onClick = {
                            project.addImageFromGallery()
                        },
                        content = {Text("Gallery")}
                    )
                    Button(
                        onClick = {
                            val internalBitmapImage = InternalBitmapImage("${count++}",mutableStateOf(null))
                            project.addInternalBitmapImage(internalBitmapImage)
                            println("Count: $count")
                                  },
                        content = {Text("Add")}
                    )
                }

                 */

            }
        }
    }
}

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns = ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}