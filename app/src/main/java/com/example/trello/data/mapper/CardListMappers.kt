package com.example.trello.data.mapper

import com.example.trello.data.local.entity.CardListEntity
import com.example.trello.domain.model.BoardList
import com.example.trello.domain.model.Card

fun CardListEntity.toDomain(cards: List<Card> = emptyList()): BoardList = BoardList(
    id = id,
    boardId = boardId,
    title = title,
    position = position,
    cards = cards
)

fun BoardList.toEntity(): CardListEntity = CardListEntity(
    id = id,
    boardId = boardId,
    title = title,
    position = position
)