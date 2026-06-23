package com.example.trello


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private const val ROUTE_BOARDS       = "boards"
private const val ROUTE_BOARD_DETAIL = "board/{boardId}"
private const val ROUTE_CARD_DETAIL  = "card/{cardId}"

@Composable
fun TrelloNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_BOARDS) {

        // Screen 1 — boards list (start destination, no back button needed)
        composable(ROUTE_BOARDS) {
            BoardsListScreen(
                onBoardClick = { boardId -> navController.navigate("board/$boardId") }
            )
        }

        // Screen 2 — board detail
        composable(
            route = ROUTE_BOARD_DETAIL,
            arguments = listOf(navArgument("boardId") { type = NavType.LongType })
        ) {
            BoardScreen(
                onNavigateBack = { navController.popBackStack() },
                onCardClick = { cardId -> navController.navigate("card/$cardId") }
            )
        }

        // Screen 3 — card detail
        composable(
            route = ROUTE_CARD_DETAIL,
            arguments = listOf(navArgument("cardId") { type = NavType.LongType })
        ) {
            CardDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}