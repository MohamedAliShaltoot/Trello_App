package com.example.trello

import com.example.trello.data.local.dao.BoardDao
import com.example.trello.data.local.dao.CardDao
import com.example.trello.data.local.dao.CardListDao
import com.example.trello.data.local.entity.BoardEntity
import com.example.trello.data.local.entity.CardEntity
import com.example.trello.data.local.entity.CardListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

data class ListWithCards(
    val list: CardListEntity,
    val cards: List<CardEntity>
)

@Singleton
class TrelloRepository @Inject constructor(
    private val boardDao: BoardDao,
    private val listDao: CardListDao,
    private val cardDao: CardDao
) {


    fun getBoards(): Flow<List<BoardEntity>> = boardDao.getAllBoards()

    suspend fun createBoard(title: String): Long =
        boardDao.insertBoard(BoardEntity(title = title))

    fun getBoardContents(boardId: Long): Flow<List<ListWithCards>> =
        listDao.getListsForBoard(boardId).flatMapLatest { lists ->
            if (lists.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(lists.map { cardDao.getCardsForList(it.id) }) { cardArrays ->
                    lists.mapIndexed { i, list -> ListWithCards(list, cardArrays[i]) }
                }
            }
        }

    suspend fun createList(boardId: Long, title: String) {
        val nextPos = (listDao.getMaxPosition(boardId) ?: 0.0) + 1.0
        listDao.insertList(CardListEntity(boardId = boardId, title = title, position = nextPos))
    }

    suspend fun deleteList(list: CardListEntity) = listDao.deleteList(list)

    suspend fun createCard(listId: Long, title: String) {
        val nextPos = (cardDao.getMaxPosition(listId) ?: 0.0) + 1.0
        cardDao.insertCard(CardEntity(listId = listId, title = title, position = nextPos))
    }
    fun getCardById(cardId: Long): Flow<CardEntity?> = cardDao.getCardById(cardId)

    suspend fun updateCardDetails(card: CardEntity, newTitle: String, newDescription: String) {
        cardDao.updateCard(card.copy(title = newTitle, description = newDescription))
    }

    suspend fun deleteCard(card: CardEntity) = cardDao.deleteCard(card)

    suspend fun getMaxPositionInList(listId: Long): Double =
        cardDao.getMaxPosition(listId) ?: 0.0

    suspend fun moveCard(card: CardEntity, newListId: Long, newPosition: Double) {
        cardDao.updateCard(card.copy(listId = newListId, position = newPosition))
    }
}