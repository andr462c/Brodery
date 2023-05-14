package me.andr4.android.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import me.andr4.android.*

@Composable
fun startScreen(projects: List<Project>) {
    projectGrid(projects)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun projectGrid(projectList: List<Project>){
    var rememberProjects = remember { projectList }
    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        contentPadding = PaddingValues(15.dp),
        state = rememberLazyListState(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(rememberProjects) { project ->
            ProjectCard(project)
        }
    }
}

@Composable
fun ProjectCard(project: Project) {
    val bitmap = project.getBitmap()
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentScale = ContentScale.FillWidth,

            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .fillMaxWidth()
                .clickable {
                    val nav = State.getNavControler()
                    nav?.navigate(Path.viewProject(project))
                }
        )
    } else {
        Box(modifier = Modifier
            .background(Color.Gray)
            .fillMaxWidth()
            .height(150.dp)
        )
    }
}