package com.example.trello


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


private const val ROUTE_BOARDS = "boards"
private const val ROUTE_BOARD_DETAIL = "board/{boardId}"

@Composable
fun TrelloNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_BOARDS) {

        composable(ROUTE_BOARDS) {
            BoardsListScreen(
                onBoardClick = { boardId -> navController.navigate("board/$boardId") }
            )
        }

        composable(
            route = ROUTE_BOARD_DETAIL,
            arguments = listOf(navArgument("boardId") { type = NavType.LongType })
        ) {
            // boardId is read out of SavedStateHandle automatically inside BoardViewModel
            BoardScreen()
        }
    }
}