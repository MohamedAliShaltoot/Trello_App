package com.example.trello.domain.model


data class Board(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val colorHex: String = "#0079BF",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val listCount: Int = 0,
    val cardCount: Int = 0,
    val completedCardCount: Int = 0
) {

    val progress: Float
        get() = if (cardCount == 0) 0f else completedCardCount / cardCount.toFloat()
}
