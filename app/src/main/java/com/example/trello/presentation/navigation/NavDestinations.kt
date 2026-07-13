package com.example.trello.presentation.navigation

sealed class NavDestination(val route: String) {

    data object BoardsList : NavDestination("boards")

    data object BoardDetail : NavDestination("board/{boardId}") {
        const val ARG_BOARD_ID = "boardId"
        fun createRoute(boardId: Long) = "board/$boardId"
    }
    data object Favorites : NavDestination("favorites")

    data object CardDetail : NavDestination("card/{cardId}") {
        const val ARG_CARD_ID = "cardId"
        fun createRoute(cardId: Long) = "card/$cardId"
    }
}
