package com.example.trello.data.repository

import com.example.trello.data.local.dao.BoardDao
import com.example.trello.data.local.dao.CardDao
import com.example.trello.data.local.dao.CardListDao
import com.example.trello.data.mapper.toDomain
import com.example.trello.data.mapper.toEntity
import com.example.trello.di.IoDispatcher
import com.example.trello.domain.model.Board
import com.example.trello.domain.model.BoardList
import com.example.trello.domain.model.Card
import com.example.trello.domain.repository.TrelloRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TrelloRepositoryImpl @Inject constructor(
    private val boardDao: BoardDao,
    private val listDao: CardListDao,
    private val cardDao: CardDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TrelloRepository {

    // Boards

    override fun observeBoards(query: String): Flow<List<Board>> =
        boardDao.getBoardsWithStats(query).map { rows -> rows.map { it.toDomain() } }

    override fun observeBoard(boardId: Long): Flow<Board?> =
        boardDao.getBoardById(boardId).map { it?.toDomain() }

    override suspend fun createBoard(title: String, description: String, colorHex: String): Long =
        withContext(ioDispatcher) {
            boardDao.insertBoard(
                Board(title = title, description = description, colorHex = colorHex).toEntity()
            )
        }

    override suspend fun updateBoard(board: Board) = withContext(ioDispatcher) {
        boardDao.updateBoard(board.toEntity())
    }

    override suspend fun deleteBoard(board: Board) = withContext(ioDispatcher) {
        boardDao.deleteBoard(board.toEntity())
    }

    override suspend fun restoreBoard(board: Board): Long = withContext(ioDispatcher) {
        // Re-insert with id = 0 so Room assigns a fresh primary key.
        boardDao.insertBoard(board.toEntity().copy(id = 0))
    }

    override suspend fun setBoardFavorite(boardId: Long, isFavorite: Boolean) =
        withContext(ioDispatcher) { boardDao.setFavorite(boardId, isFavorite) }

    // Lists

    override fun observeLists(boardId: Long): Flow<List<BoardList>> =
        listDao.getListsForBoard(boardId).flatMapLatest { lists ->
            if (lists.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(lists.map { list -> cardDao.getCardsForList(list.id) }) { cardArrays ->
                    lists.mapIndexed { index, list ->
                        list.toDomain(cardArrays[index].map { it.toDomain() })
                    }
                }
            }
        }

    override suspend fun createList(boardId: Long, title: String): Long = withContext(ioDispatcher) {
        val nextPosition = (listDao.getMaxPosition(boardId) ?: 0.0) + 1.0
        listDao.insertList(
            BoardList(boardId = boardId, title = title, position = nextPosition).toEntity()
        )
    }

    override suspend fun deleteList(list: BoardList) = withContext(ioDispatcher) {
        listDao.deleteList(list.toEntity())
    }

    override suspend fun restoreList(list: BoardList): Long = withContext(ioDispatcher) {
        listDao.insertList(list.toEntity().copy(id = 0))
    }

    // Cards

    override fun observeCard(cardId: Long): Flow<Card?> =
        cardDao.getCardById(cardId).map { it?.toDomain() }

    override suspend fun createCard(listId: Long, title: String): Long = withContext(ioDispatcher) {
        val nextPosition = (cardDao.getMaxPosition(listId) ?: 0.0) + 1.0
        cardDao.insertCard(Card(listId = listId, title = title, position = nextPosition).toEntity())
    }

    override suspend fun updateCard(card: Card) = withContext(ioDispatcher) {
        cardDao.updateCard(card.toEntity())
    }

    override suspend fun deleteCard(card: Card) = withContext(ioDispatcher) {
        cardDao.deleteCard(card.toEntity())
    }

    override suspend fun restoreCard(card: Card): Long = withContext(ioDispatcher) {
        cardDao.insertCard(card.toEntity().copy(id = 0))
    }

    override suspend fun setCardCompleted(cardId: Long, completed: Boolean) =
        withContext(ioDispatcher) { cardDao.setCompleted(cardId, completed) }

    override suspend fun moveCard(card: Card, targetListId: Long) = withContext(ioDispatcher) {
        val nextPosition = (cardDao.getMaxPosition(targetListId) ?: 0.0) + 1.0
        cardDao.updateCard(card.copy(listId = targetListId, position = nextPosition).toEntity())
    }
}
