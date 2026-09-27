package com.erebuni782.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.erebuni782.app.MainViewModel
import com.erebuni782.app.R
import com.erebuni782.app.ui.aerial.AerialScreen
import com.erebuni782.app.ui.aerial.AerialSessionScreen
import com.erebuni782.app.ui.employee.ArtifactEditScreen
import com.erebuni782.app.ui.employee.EmployeeScreen
import com.erebuni782.app.ui.employee.PinGateScreen
import com.erebuni782.app.ui.guide.GuideScreen
import com.erebuni782.app.ui.library.LibraryScreen
import com.erebuni782.app.ui.library.MiniPlayerBar
import com.erebuni782.app.ui.library.rememberLibraryViewModel
import com.erebuni782.app.ui.reader.ReaderScreen
import com.erebuni782.app.ui.settings.SettingsScreen

private data class TabSpec(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    TabSpec("guide", R.string.nav_guide, Icons.Outlined.Home),
    TabSpec("wiki", R.string.nav_wiki, Icons.Outlined.Info),
    TabSpec("library", R.string.nav_library, Icons.Outlined.Book),
    TabSpec("map", R.string.nav_map, Icons.Outlined.Place),
    TabSpec("settings", R.string.nav_settings, Icons.Outlined.Settings)
)

/** Туристический шелл P2: 5 вкладок + мини-плеер над навигацией. */
@Composable
fun MainShell(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val libraryViewModel = rememberLibraryViewModel()

    Scaffold(
        bottomBar = {
            Column {
                MiniPlayerBar(libraryViewModel)
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) },
                            modifier = Modifier.testTag("nav_${tab.route}")
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "guide",
            modifier = Modifier.padding(padding)
        ) {
            composable("guide") {
                GuideScreen(onOpenArticle = { navController.navigate("wiki_article/$it") })
            }
            composable("wiki") {
                WikiScreen(onOpenArticle = { navController.navigate("wiki_article/$it") })
            }
            composable("wiki_article/{id}") { entry ->
                WikiArticleScreen(articleId = entry.arguments?.getString("id").orEmpty())
            }
            composable("library") {
                LibraryScreen(onOpenBook = { navController.navigate("book/$it") })
            }
            composable("book/{id}") { entry ->
                ReaderScreen(
                    bookId = entry.arguments?.getString("id").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable("map") { MapScreen() }
            composable("settings") { SettingsScreen(mainViewModel) { navController.navigate("employee_gate") } }
            composable("employee_gate") {
                PinGateScreen(
                    onUnlocked = {
                        navController.navigate("employee") {
                            popUpTo("employee_gate") { inclusive = true }
                        }
                    }
                )
            }
            composable("employee") {
                // guard: после recreate процесса сессия закрыта — редирект на PIN-гейт
                if (com.erebuni782.app.data.EmployeeSession.unlocked) {
                    EmployeeScreen(
                        onOpenArtifact = { navController.navigate("artifact_edit/$it") },
                        onNewArtifact = { navController.navigate("artifact_edit/new") },
                        onOpenAerial = { navController.navigate("aerial") }
                    )
                } else {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        navController.navigate("employee_gate") {
                            popUpTo("employee") { inclusive = true }
                        }
                    }
                }
            }
            composable("aerial") {
                if (com.erebuni782.app.data.EmployeeSession.unlocked) {
                    AerialScreen(onOpenSession = { navController.navigate("aerial_session/$it") })
                } else {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        navController.navigate("employee_gate") {
                            popUpTo("aerial") { inclusive = true }
                        }
                    }
                }
            }
            composable("aerial_session/{id}") { entry ->
                if (com.erebuni782.app.data.EmployeeSession.unlocked) {
                    AerialSessionScreen(
                        sessionId = entry.arguments?.getString("id").orEmpty(),
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        navController.navigate("employee_gate") {
                            popUpTo("aerial_session/{id}") { inclusive = true }
                        }
                    }
                }
            }
            composable("artifact_edit/{id}") { entry ->
                val raw = entry.arguments?.getString("id").orEmpty()
                ArtifactEditScreen(
                    artifactId = if (raw == "new") "" else raw,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
