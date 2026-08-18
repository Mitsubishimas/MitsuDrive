package com.mitsudrive.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mitsudrive.app.routes.*
import com.mitsudrive.app.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Map : Screen("map")
    object Chats : Screen("chats")
    object ChatDetail : Screen("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
    object Garage : Screen("garage")
    object Sos : Screen("sos")
    object Settings : Screen("settings")
}

@Composable
fun DriveNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route
    ) {
        // Лента
        composable(Screen.Feed.route) {
            FeedRoute(
                onPostClick = { postId ->
                    // TODO: Переход к деталям поста
                },
                onCreatePost = {
                    // TODO: Открыть создание поста
                }
            )
        }
        
        // Карта
        composable(Screen.Map.route) {
            MapRoute()
        }
        
        // Чаты
        composable(Screen.Chats.route) {
            ChatListRoute(
                onChatClick = { chatId ->
                    navController.navigate(Screen.ChatDetail.createRoute(chatId))
                }
            )
        }
        
        // Детали чата
        composable(Screen.ChatDetail.route) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailRoute(
                chatId = chatId,
                onBack = { navController.popBackStack() }
            )
        }
        
        // Гараж
        composable(Screen.Garage.route) {
            GarageRoute()
        }
        
        // SOS
        composable(Screen.Sos.route) {
            SosRoute()
        }
        
        // Настройки
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
