package com.example.trello.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trello.R
import com.example.trello.presentation.board.BoardScreen
import com.example.trello.presentation.boards.BoardsListScreen
import com.example.trello.presentation.carddetail.CardDetailScreen
import com.example.trello.presentation.favorites.FavoritesScreen


private val topLevelRoutes = setOf(NavDestination.BoardsList.route, NavDestination.Favorites.route)

@Composable
fun TrelloNavGraph() {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val showBottomBar = currentRoute?.destination?.route in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                TrelloBottomBar(
                    navController = navController,
                    currentRoute = currentRoute?.destination?.route
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.BoardsList.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // Tab 1 — boards dashboard
            composable(NavDestination.BoardsList.route) {
                BoardsListScreen(
                    onBoardClick = { boardId ->
                        navController.navigate(NavDestination.BoardDetail.createRoute(boardId))
                    }
                )
            }

            // Tab 2 — favorite boards only
            composable(NavDestination.Favorites.route) {
                FavoritesScreen(
                    onBoardClick = { boardId ->
                        navController.navigate(NavDestination.BoardDetail.createRoute(boardId))
                    }
                )
            }

            // Board detail (lists + cards) — pushed on top, no bottom bar
            composable(
                route = NavDestination.BoardDetail.route,
                arguments = listOf(navArgument(NavDestination.BoardDetail.ARG_BOARD_ID) { type = NavType.LongType })
            ) {
                BoardScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCardClick = { cardId ->
                        navController.navigate(NavDestination.CardDetail.createRoute(cardId))
                    }
                )
            }

            // Card detail — pushed on top, no bottom bar
            composable(
                route = NavDestination.CardDetail.route,
                arguments = listOf(navArgument(NavDestination.CardDetail.ARG_CARD_ID) { type = NavType.LongType })
            ) {
                CardDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun TrelloBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == NavDestination.BoardsList.route,
            onClick = { navController.navigateToTab(NavDestination.BoardsList.route) },
            icon = { Image(
                painterResource(id= R.drawable.dashboard),
                contentDescription = null,

            ) },
            label = { Text("Boards",color = if(currentRoute == NavDestination.BoardsList.route) Color.Black else Color.Gray) }
        )
        NavigationBarItem(
            selected = currentRoute == NavDestination.Favorites.route,
            onClick = { navController.navigateToTab(NavDestination.Favorites.route) },

            icon = { Image(
                painterResource(id= R.drawable.favorite),
                contentDescription = null,

                ) },
            label = { Text("Favorites" ,color = if(currentRoute == NavDestination.Favorites.route) Color.Black else Color.Gray) }
        )
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
