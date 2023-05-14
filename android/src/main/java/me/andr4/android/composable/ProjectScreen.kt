package me.andr4.android.composable

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.andr4.android.AppState
import me.andr4.android.Project
import me.andr4.android.R

@Composable
fun viewProjectScreen(project: Project?) {
    val imageBitmap = project?.getBitmap()?.asImageBitmap() ?: return

    projectView(project)
}

@Composable
fun editProjectScreen(appState: AppState, project: Project?) {
    val imageBitmap = project?.getBitmap()?.asImageBitmap() ?: return

    Column{
        Box(
            modifier = Modifier.background(Color.Blue)
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        }
        Box( //Bottom half
            modifier = Modifier.background(Color.Red)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            FloatingActionButton(
                onClick = {appState.navigateToviewProject(project)},
                content = {Text("Save")},
            )
        }
    }

    /*
    Image(
        bitmap = imageBitmap,
        contentDescription = null
    )

     */
}

@Composable
fun createProjectScreen(appState: AppState) {
    val projects = appState.projects
    Spacer(Modifier.size(10.dp))
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(20.dp),
    ) {
        Text(
            text = "Take a photo or add one from your gallery",
            fontSize = MaterialTheme.typography.h3.fontSize,
            fontStyle = MaterialTheme.typography.h3.fontStyle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomEnd),
        ) {
            Button(
                onClick = {
                    projects.startNewProjectFromCamera(appState)
                          },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(150.dp)
            ) {
                Column {
                    Icon(
                        painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text("Photo")
                }
            }

            Button(
                onClick = {projects.startNewProjectFromGallery(appState)},
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(150.dp)
            ) {
                Column {
                    Icon(
                        painterResource(R.drawable.outline_collections_24),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text("Gallery")
                }
            }

        }
    }
}