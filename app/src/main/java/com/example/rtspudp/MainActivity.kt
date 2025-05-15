package com.example.rtspudp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rtspudp.database.AppDatabase
import com.example.rtspudp.repository.StreamRepository
import com.example.rtspudp.screens.admin.AdminDashboardScreen
import com.example.rtspudp.screens.admin.UserManagementScreen
import com.example.rtspudp.screens.auth.LoginScreen
import com.example.rtspudp.screens.auth.RegisterScreen
import com.example.rtspudp.screens.user.UserStreamsScreen
import com.example.rtspudp.screens.video.VideoScreen
import com.example.rtspudp.viewmodels.AuthViewModel
import com.example.rtspudp.viewmodels.StreamViewModel
import com.example.rtspudp.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val database = AppDatabase.getDatabase(context)
            val repository = StreamRepository(
                database.userDao(),
                database.userStreamDao()
            )

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.Factory(repository)
            )
            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModel.Factory(repository)
            )
            val streamViewModel: StreamViewModel = viewModel(
                factory = StreamViewModel.Factory(repository)
            )

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { isAdmin ->
                            if (isAdmin) {
                                navController.navigate("admin_dashboard")
                            } else {
                                navController.navigate("user_streams/${authViewModel.currentUserId}")
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate("register")
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterSuccess = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate("login")
                        }
                    )
                }

                composable("admin_dashboard") {
                    AdminDashboardScreen(
                        userViewModel = userViewModel,
                        onUserSelected = { userId ->
                            navController.navigate("user_management/$userId")
                        },
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("admin_dashboard") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    "user_management/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                    UserManagementScreen(
                        userId = userId,
                        streamViewModel = streamViewModel,
                        onBack = { navController.popBackStack() },
                        navController = navController // Добавлен navController
                    )
                }

                composable(
                    "user_streams/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                    UserStreamsScreen(
                        userId = userId,
                        streamViewModel = streamViewModel,
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("user_streams/{userId}") { inclusive = true }
                            }
                        },
                        navController = navController
                    )
                }

                composable(
                    "video/{streamId}",
                    arguments = listOf(navArgument("streamId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val streamId = backStackEntry.arguments?.getInt("streamId") ?: 0
                    val streams by streamViewModel.streams.collectAsState()
                    val stream = streams.find { it.id == streamId }

                    VideoScreen(
                        navController = navController,
                        stream = stream
                    )
                }
            }
        }
    }
}