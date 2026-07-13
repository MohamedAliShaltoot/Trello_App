package com.example.trello.data.mapper

import com.example.trello.data.local.dao.BoardWithStats
import com.example.trello.data.local.entity.BoardEntity
import com.example.trello.domain.model.Board

fun BoardWithStats.toDomain(): Board = Board(
    id = id,
    title = title,
    description = description,
    colorHex = colorHex,
    isFavorite = isFavorite,
    createdAt = createdAt,
    listCount = listCount,
    cardCount = cardCount,
    completedCardCount = completedCardCount
)

fun BoardEntity.toDomain(): Board = Board(
    id = id,
    title = title,
    description = description,
    colorHex = colorHex,
    isFavorite = isFavorite,
    createdAt = createdAt
)

fun Board.toEntity(): BoardEntity = BoardEntity(
    id = id,
    title = title,
    description = description,
    colorHex = colorHex,
    isFavorite = isFavorite,
    createdAt = createdAt
)