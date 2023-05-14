package me.andr4.android

sealed class Screen(val route: String) {
    object StartScreen : Screen("start_screen")
    object ProjectScreen : Screen("project_screen") {
        object create : Screen("$route/create")
        object edit : Screen("$route/edit/") {
            fun with(project: Project): String {
                return "$route/${project.getName()}"
            }
        }

        object view : Screen("$route/view"){
            fun with(project: Project): String {
                return "$route/${project.getName()}"
            }
        }
    }

    fun withArhs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

object Path {
    fun createProject(): String {
        return Screen.ProjectScreen.create.route
    }

    fun viewProject(project: Project): String {
        return Screen.ProjectScreen.view.with(project)
    }

    fun editProject(project: Project): String {
        return Screen.ProjectScreen.edit.with(project)
    }
}
