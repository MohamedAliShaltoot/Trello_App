package com.example.trello.domain.model

data class BoardList(
    val id: Long = 0,
    val boardId: Long,
    val title: String,
    val position: Double,
    val cards: List<Card> = emptyList()
) {
    val cardCount: Int get() = cards.size
    val completedCount: Int get() = cards.count { it.isCompleted }
}
