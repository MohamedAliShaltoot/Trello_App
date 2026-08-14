package com.example.trello.data.mapper

import com.example.trello.data.local.dao.BoardWithStats
import com.example.trello.data.local.entity.BoardEntity
import com.example.trello.data.local.entity.CardEntity
import com.example.trello.data.local.entity.CardListEntity
import com.example.trello.domain.model.Board
import com.example.trello.domain.model.BoardList
import com.example.trello.domain.model.Card
import com.example.trello.domain.model.Priority

// ---- Board --------------------------------------------------------------

//fun BoardWithStats.toDomain(): Board = Board(
//    id = id,
//    title = title,
//    description = description,
//    colorHex = colorHex,
//    isFavorite = isFavorite,
//    createdAt = createdAt,
//    listCount = listCount,
//    cardCount = cardCount,
//    completedCardCount = completedCardCount
//)
//
//fun BoardEntity.toDomain(): Board = Board(
//    id = id,
//    title = title,
//    description = description,
//    colorHex = colorHex,
//    isFavorite = isFavorite,
//    createdAt = createdAt
//)
//
//fun Board.toEntity(): BoardEntity = BoardEntity(
//    id = id,
//    title = title,
//    description = description,
//    colorHex = colorHex,
//    isFavorite = isFavorite,
//    createdAt = createdAt
//)

// ---- CardList -------------------------------------------------------------

//fun CardListEntity.toDomain(cards: List<Card> = emptyList()): BoardList = BoardList(
//    id = id,
//    boardId = boardId,
//    title = title,
//    position = position,
//    cards = cards
//)
//
//fun BoardList.toEntity(): CardListEntity = CardListEntity(
//    id = id,
//    boardId = boardId,
//    title = title,
//    position = position
//)

// ---- Card -------------------------------------------------------------

//fun CardEntity.toDomain(): Card = Card(
//    id = id,
//    listId = listId,
//    title = title,
//    description = description,
//    position = position,
//    priority = Priority.fromNameOrDefault(priority),
//    dueDate = dueDate,
//    isCompleted = isCompleted,
//    createdAt = createdAt
//)
//
//fun Card.toEntity(): CardEntity = CardEntity(
//    id = id,
//    listId = listId,
//    title = title,
//    description = description,
//    position = position,
//    priority = priority.name,
//    dueDate = dueDate,
//    isCompleted = isCompleted,
//    createdAt = createdAt
//)
