package xyz.myeoru.chatappexample.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import xyz.myeoru.chatappexample.feature.main.screen.ChatScreen
import xyz.myeoru.chatappexample.feature.main.screen.FriendScreen
import xyz.myeoru.chatappexample.feature.main.screen.MoreScreen
import xyz.myeoru.chatappexample.ui.theme.ChatAppExampleTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatAppExampleTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            topLevelRoutes.forEach { topLevelRoute ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(topLevelRoute.iconResId),
                                            contentDescription = topLevelRoute.name
                                        )
                                    },
                                    label = { Text(topLevelRoute.name) },
                                    selected = currentDestination?.hierarchy?.any {
                                        it.hasRoute(topLevelRoute.route::class)
                                    } == true,
                                    onClick = {
                                        navController.navigate(topLevelRoute.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = FriendRoute,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { fadeOut() }
                    ) {
                        composable<FriendRoute> {
                            FriendScreen(
                                innerPadding = innerPadding
                            )
                        }
                        composable<ChatRoute> {
                            ChatScreen(
                                innerPadding = innerPadding
                            )
                        }
                        composable<MoreRoute> {
                            MoreScreen(
                                innerPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}