package com.example.trello.presentation.board.components.draganddrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.example.trello.domain.model.Card
import com.example.trello.domain.model.BoardList

class DragDropState {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero) // Local touch offset inside card
    var dragOffset by mutableStateOf(Offset.Zero) // Drag delta
    var cardPosition by mutableStateOf(Offset.Zero) // Global position of the card/list when drag starts
    var draggedCard by mutableStateOf<Card?>(null)
    var draggedList by mutableStateOf<BoardList?>(null)
    
    // Bounds of all lists on the screen
    private val listBounds = mutableMapOf<Long, Rect>()
    // Bounds of all cards on the screen: Card ID -> (List ID, Rect)
    private val cardBounds = mutableMapOf<Long, Pair<Long, Rect>>()

    fun onDragStart(offset: Offset, card: Card, cardGlobalPos: Offset) {
        dragPosition = offset
        dragOffset = Offset.Zero
        cardPosition = cardGlobalPos
        isDragging = true
        draggedCard = card
        draggedList = null
    }

    fun onListDragStart(offset: Offset, list: BoardList, listGlobalPos: Offset) {
        dragPosition = offset
        dragOffset = Offset.Zero
        cardPosition = listGlobalPos
        isDragging = true
        draggedList = list
        draggedCard = null
    }

    fun onDrag(offset: Offset) {
        dragOffset += offset
    }

    fun onDragInterrupt() {
        isDragging = false
        draggedCard = null
        draggedList = null
        dragOffset = Offset.Zero
        dragPosition = Offset.Zero
        cardPosition = Offset.Zero
    }

    fun registerListBounds(listId: Long, bounds: Rect) {
        listBounds[listId] = bounds
    }

    fun registerCardBounds(cardId: Long, listId: Long, bounds: Rect) {
        cardBounds[cardId] = listId to bounds
    }

    fun unregisterCard(cardId: Long) {
        cardBounds.remove(cardId)
    }

    // Returns (TargetListId, IsLeftHalf) for dropping lists
    fun findTargetList(position: Offset): Pair<Long, Boolean>? {
        for ((listId, rect) in listBounds) {
            if (rect.contains(position)) {
                val isLeftHalf = position.x < rect.center.x
                return Pair(listId, isLeftHalf)
            }
        }
        return null
    }

    // Returns (TargetListId, TargetCardId, IsTopHalf)
    fun findTargetCard(position: Offset): Triple<Long, Long, Boolean>? {
        for ((cardId, pair) in cardBounds) {
            val (listId, rect) = pair
            if (rect.contains(position)) {
                val isTopHalf = position.y < rect.center.y
                return Triple(listId, cardId, isTopHalf)
            }
        }
        return null
    }
}

val LocalDragDropState = compositionLocalOf { DragDropState() }

@Composable
fun rememberDragDropState(): DragDropState {
    return remember { DragDropState() }
}
