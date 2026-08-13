package com.example.trello.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trello.data.local.entity.BoardEntity
import kotlinx.coroutines.flow.Flow


data class BoardWithStats(
    val id: Long,
    val title: String,
    val description: String,
    val colorHex: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val listCount: Int,
    val cardCount: Int,
    val completedCardCount: Int
)

@Dao
interface BoardDao {

    @Query(
        """
        SELECT
            b.id AS id,
            b.title AS title,
            b.description AS description,
            b.colorHex AS colorHex,
            b.isFavorite AS isFavorite,
            b.createdAt AS createdAt,
            (SELECT COUNT(*) FROM lists l WHERE l.boardId = b.id) AS listCount,
            (SELECT COUNT(*) FROM cards c
                JOIN lists l2 ON c.listId = l2.id
                WHERE l2.boardId = b.id) AS cardCount,
            (SELECT COUNT(*) FROM cards c2
                JOIN lists l3 ON c2.listId = l3.id
                WHERE l3.boardId = b.id AND c2.isCompleted = 1) AS completedCardCount
        FROM boards b
        WHERE (:query = '' OR b.title LIKE '%' || :query || '%')
        ORDER BY b.isFavorite DESC, b.createdAt DESC
        """
    )
    fun getBoardsWithStats(query: String = ""): Flow<List<BoardWithStats>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoardById(boardId: Long): Flow<BoardEntity?>

    @Insert
    suspend fun insertBoard(board: BoardEntity): Long

    @Update
    suspend fun updateBoard(board: BoardEntity)

    @Delete
    suspend fun deleteBoard(board: BoardEntity)

    @Query("UPDATE boards SET isFavorite = :isFavorite WHERE id = :boardId")
    suspend fun setFavorite(boardId: Long, isFavorite: Boolean)
}
