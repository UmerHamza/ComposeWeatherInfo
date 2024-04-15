package com.appdev.weathercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appdev.weathercompose.ui.theme.DARK_BLUE
import com.appdev.weathercompose.ui.theme.OFF_BLACK
import com.appdev.weathercompose.ui.theme.OFF_WHITE
import com.appdev.weathercompose.ui.theme.WeatherComposeTheme
import com.appdev.weathercompose.view.NotesView
import com.appdev.weathercompose.view.Routes
import com.appdev.weathercompose.view.dashboard.AddNoteView
import com.appdev.weathercompose.view.dashboard.HomeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            WeatherComposeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = OFF_BLACK,
                    bottomBar = { BottomNavigation(navController = navController) }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Notes.screen_route
                    ) {
                        composable(BottomNavItem.Notes.screen_route) { NotesView(navController) }
                        composable(BottomNavItem.Home.screen_route) {
                            HomeView(
                                navController,
                                padding
                            )
                        }
                        composable(Routes.ADD_NOTES) { backStackEntry ->
                            val info = backStackEntry.arguments?.getString("notesInfo")
                            AddNoteView(notesInfo = info, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Notes,
        BottomNavItem.Home
    )

    androidx.compose.material.BottomNavigation(
        backgroundColor = DARK_BLUE,
        contentColor = OFF_BLACK
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = OFF_WHITE,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {
                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

}

sealed class BottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    object Notes : BottomNavItem("Notes", R.drawable.ic_baseline_notes_24, Routes.NOTES)
    object Home : BottomNavItem("Home", R.drawable.ic_baseline_home_24, Routes.HOME)
}
