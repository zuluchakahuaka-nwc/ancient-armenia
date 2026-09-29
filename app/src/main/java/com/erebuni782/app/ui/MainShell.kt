package com.erebuni782.app.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.erebuni782.app.AppGraph
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

import com.erebuni782.app.ui.topbar.TopActionBar

private data class TabSpec(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    TabSpec("guide", R.string.nav_guide, Icons.Outlined.Home),
    TabSpec("wiki", R.string.nav_wiki, Icons.Outlined.Info),
    TabSpec("library", R.string.nav_library, Icons.Outlined.Book),
    TabSpec("map", R.string.nav_map, Icons.Outlined.Place),
    TabSpec("settings", R.string.nav_settings, Icons.Outlined.Settings),
    TabSpec("staff", R.string.nav_staff, Icons.Outlined.AdminPanelSettings)
)

/**
 * Туристический шелл P2. P6: адаптив — узкий экран = bottom-bar,
 * широкий (планшет ≥840dp) = navigation-rail слева. Иконки несут
 * contentDescription (TalkBack, AGENTS.md P6).
 */
@Composable
fun MainShell(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val libraryViewModel = rememberLibraryViewModel()
    val modeSelected by AppGraph.settings.modeSelected.collectAsState(initial = false)
    val onboardingShown by AppGraph.settings.onboardingShown.collectAsState(initial = true)
    val langSelected by AppGraph.settings.langSelected.collectAsState(initial = true)

    fun navigateTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val startDestination = if (!modeSelected) "welcome" else "guide"

    BoxWithConstraints {
        val wide = maxWidth >= 840.dp

        // ── АВТОСТАРТ Urartu.fm: играет СРАЗУ при запуске, даже во время онбординга ──
        androidx.compose.runtime.LaunchedEffect(Unit) {
            val on = AppGraph.settings.urartuFmEnabled.first()
            if (on && !libraryViewModel.playerState.value.hasContent) {
                libraryViewModel.setStation(true)
            }
        }

        // ── 0. ВЫБОР ЯЗЫКА (самый первый запуск) ──
        if (!langSelected) {
            com.erebuni782.app.ui.LanguagePickerScreen(
                onLangSelected = { mainViewModel.setLangSelected() },
                onSkip = { mainViewModel.setLangSelected() }
            )
        } else if (!onboardingShown) {
            // ── 1. ОНБОРДИНГ: 5 слайдов ──
            com.erebuni782.app.ui.OnboardingScreen(
                onFinished = { mainViewModel.setOnboardingShown() }
            )
        } else if (!modeSelected) {
            // ── 2. ВЫБОР РЕЖИМА ──
            com.erebuni782.app.ui.WelcomeScreen(
                onTourist = { mainViewModel.selectMode() },
                onEmployee = { mainViewModel.selectMode() }
            )
        } else if (wide) {
            // ── ПЛАНШЕТ: rail слева + стилизованный контент ──
            com.erebuni782.app.ui.theme.StyledScreen {
                Column(Modifier.fillMaxSize()) {
                    TopActionBar(libraryViewModel)
                    MiniPlayerBar(libraryViewModel)
                    Row(Modifier.fillMaxHeight()) {
                        NavigationRail {
                            tabs.forEach { tab ->
                                NavigationRailItem(
                                    selected = currentRoute == tab.route,
                                    onClick = { navigateTab(tab.route) },
                                    icon = {
                                        Icon(tab.icon, contentDescription = stringResource(tab.labelRes))
                                    },
                                    label = { Text(stringResource(tab.labelRes)) },
                                    modifier = Modifier.testTag("nav_${tab.route}")
                                )
                            }
                        }
                        AppNavHost(navController, mainViewModel, Modifier.fillMaxHeight().weight(1f))
                    }
                }
            }
        } else {
            // ── ТЕЛЕФОН: стилизованный контент + bottom bar ──
            com.erebuni782.app.ui.theme.StyledScreen {
                Scaffold(
                    topBar = { TopActionBar(libraryViewModel) },
                    bottomBar = {
                        Column {
                            MiniPlayerBar(libraryViewModel)
                            NavigationBar {
                                tabs.forEach { tab ->
                                    NavigationBarItem(
                                        selected = currentRoute == tab.route,
                                        onClick = { navigateTab(tab.route) },
                                        icon = {
                                            Icon(tab.icon, contentDescription = stringResource(tab.labelRes))
                                        },
                                        label = { Text(stringResource(tab.labelRes)) },
                                        modifier = Modifier.testTag("nav_${tab.route}")
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    AppNavHost(navController, mainViewModel, Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "guide", modifier = modifier) {
        composable("guide") {
            GuideScreen(
                onOpenArticle = { navController.navigate("wiki_article/$it") },
                onOpenTimeline = { navController.navigate("timeline") }
            )
        }
        composable("timeline") {
            com.erebuni782.app.ui.guide.TimelineScreen()
        }
        composable("wiki") {
            WikiScreen(onOpenArticle = { navController.navigate("wiki_article/$it") })
        }
        composable("wiki_article/{id}") { entry ->
            WikiArticleScreen(articleId = entry.arguments?.getString("id").orEmpty())
        }
        composable("library") {
            LibraryScreen(
                onOpenBook = { navController.navigate("book/$it") },
                onOpenPdf = { navController.navigate("pdf/$it") }
            )
        }
        composable("pdf/{id}") { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            val context = androidx.compose.ui.platform.LocalContext.current
            val book = com.erebuni782.app.data.book.PdfBookCatalog.find(context, id)
            if (book != null) {
                com.erebuni782.app.ui.reader.PdfReaderScreen(book = book, onBack = { navController.popBackStack() })
            }
        }
        composable("book/{id}") { entry ->
            ReaderScreen(
                bookId = entry.arguments?.getString("id").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
        composable("map") { MapScreen() }
        composable("settings") { SettingsScreen(mainViewModel) { navController.navigate("employee_gate") } }
        composable("staff") {
            // прямая вкладка входа в режим сотрудника
            androidx.compose.runtime.LaunchedEffect(Unit) {
                if (com.erebuni782.app.data.EmployeeSession.unlocked) {
                    navController.navigate("employee") { launchSingleTop = true }
                } else {
                    navController.navigate("employee_gate") {
                        popUpTo("staff") { inclusive = true }
                    }
                }
            }
        }
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

fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
