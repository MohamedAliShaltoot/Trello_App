package com.example.trello.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trello.data.local.entity.BoardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {

    @Query("SELECT * FROM boards ORDER BY createdAt DESC")
    fun getAllBoards(): Flow<List<BoardEntity>>

    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoardById(boardId: Long): Flow<BoardEntity?>

    @Insert
    suspend fun insertBoard(board: BoardEntity): Long

    @Update
    suspend fun updateBoard(board: BoardEntity)

    @Delete
    suspend fun deleteBoard(board: BoardEntity)
}