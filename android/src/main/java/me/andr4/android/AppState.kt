package me.andr4.android

import android.content.Context
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

enum class AppOverlay{
    STARTPAGE, NONE
}

class AppState(
    val navController: NavHostController,
    val projects: Projects,
    private var showBottomBar: MutableState<Boolean>,
    private var showActionButton: MutableState<Boolean>,
) {
    init {
        val navigationlistener = NavController.OnDestinationChangedListener {
                _, destination, _ -> updateState(destination.route ?: "")
        }
        navController.addOnDestinationChangedListener(navigationlistener)

    }
    fun showShowBottomBar() : Boolean {
        return showBottomBar.value
    }

    fun showActionButton() : Boolean {
        return showActionButton.value
    }

    fun setShowActionButton(show: Boolean) {
        showActionButton.value = show
    }

    fun setOverlayState(state: AppOverlay) {
        when (state) {
            AppOverlay.STARTPAGE -> {
                showActionButton.value = true
            }
            else -> {
                showActionButton.value = false
            }
        }
    }

    private fun updateState(route: String) {
        when {
            route.startsWith(Screen.StartScreen.route) -> stateStart()
            else -> stateNothing()
        }
    }

    fun stateStart(){
        showActionButton.value = true
        showBottomBar.value = false
    }
    fun stateNothing(){
        showActionButton.value = false
        showBottomBar.value = false
    }

    fun navigateToCreateProject() {
        navController.navigate(Path.createProject())
    }

    fun navigateToviewProject(project: Project) {
        navController.navigate(Path.viewProject(project))
    }

    fun navigateToEditProject(project: Project) {
        navController.navigate(Path.editProject(project))
    }
}

object State{
    var appState: AppState? = null

    fun getNavControler() : NavHostController? {
        return  appState?.navController
    }
}

@Composable
fun rememberAppState(
    projects: Projects,
    navController: NavHostController = rememberNavController(),
    showActionButton: MutableState<Boolean> = mutableStateOf(true),
    showBottomBar: MutableState<Boolean> = mutableStateOf(false)
): AppState {
    val appState = remember(navController, showActionButton) {
        AppState(navController, projects,
            showBottomBar = showBottomBar,
            showActionButton = showActionButton)
    }
    State.appState = appState
    return appState
}