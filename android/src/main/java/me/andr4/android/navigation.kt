package me.andr4.android

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.whenCreated
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import androidx.lifecycle.withStarted
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.andr4.android.composable.*

@Composable
fun Navigation(appState: AppState) {
    val startDestination = Screen.StartScreen.route
    val navController = appState.navController

    NavHost(navController = navController, startDestination = startDestination) {
        //Start screen
        composable(route = Screen.StartScreen.route) {
            val projectList = appState.projects.projects
            startScreen(projectList)
        }

        // Project screen
        composable(
            route = Screen.ProjectScreen.view.route + "/{name}",
            arguments = listOf(navArgument("name"){
                type = NavType.StringType
            })
        ) {

            val project = findProject(it, appState)

            viewProjectScreen(project)
        }

        // Project Creation Screen
        composable(route = Path.createProject()) {
            createProjectScreen(appState)
        }

        composable(
            route = Screen.ProjectScreen.edit.route + "/{name}",
            arguments = listOf(navArgument("name"){
                type = NavType.StringType
            })
        ) {

            val project = findProject(it, appState)

            editProjectScreen(appState, project)
        }
    }
}

@Composable
fun findProject(navBackStackEntry: NavBackStackEntry, appState: AppState): Project? {
    val name = navBackStackEntry.arguments?.getString("name")

    val project = appState.projects.projects.find { it.getName() == name }

    if (project == null) {
        Toast.makeText(LocalContext.current, "Failed to find project", Toast.LENGTH_SHORT).show()
    }
    return project
}

//TODO load kun nødvendige billeder.

//TODO Move startSCreen
/*
@Composable
fun StartScreen(navController: NavController) {
    Column{
        Text("This is the start screen")
        Button(
            onClick = {navController.navigate(Screen.ProjectScreen.withArhs("lol"))},
            content = { Text("Press me") }
        )
    }
}

@Composable
fun ProjectScreen(name: String) {
    Text(name)
}

 */