package com.example.trello.domain.model

data class Card(
    val id: Long = 0,
    val listId: Long,
    val title: String,
    val description: String = "",
    val position: Double,
    val priority: Priority = Priority.NONE,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isOverdue: Boolean
        get() = dueDate != null && !isCompleted && dueDate < System.currentTimeMillis()
}
