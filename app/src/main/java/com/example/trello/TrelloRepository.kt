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

/** A list paired with its current cards - what the UI actually wants to render per column. */
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

    /**
     * Streams every list on a board, each paired with its own live card list.
     * Re-emits automatically whenever lists OR any card in any of those lists changes,
     * because flatMapLatest restarts the inner combine() if the set of lists changes.
     */
    fun getBoardContents(boardId: Long): Flow<List<ListWithCards>> =
        listDao.getListsForBoard(boardId).flatMapLatest { lists ->
            if (lists.isEmpty()) {
                flowOf(emptyList())
            } else {
                val cardFlowsPerList = lists.map { list -> cardDao.getCardsForList(list.id) }
                combine(cardFlowsPerList) { cardArrays ->
                    lists.mapIndexed { index, list -> ListWithCards(list, cardArrays[index]) }
                }
            }
        }

    suspend fun createList(boardId: Long, title: String) {
        val nextPosition = (listDao.getMaxPosition(boardId) ?: 0.0) + 1.0
        listDao.insertList(
            CardListEntity(
                boardId = boardId,
                title = title,
                position = nextPosition
            )
        )
    }

    suspend fun createCard(listId: Long, title: String) {
        val nextPosition = (cardDao.getMaxPosition(listId) ?: 0.0) + 1.0
        cardDao.insertCard(CardEntity(listId = listId, title = title, position = nextPosition))
    }

    /** Used by the drag-and-drop gesture handler once you wire that up. */
    suspend fun moveCard(card: CardEntity, newListId: Long, newPosition: Double) {
        cardDao.updateCard(card.copy(listId = newListId, position = newPosition))
    }

    suspend fun deleteCard(card: CardEntity) = cardDao.deleteCard(card)

    suspend fun deleteList(list: CardListEntity) = listDao.deleteList(list)
}