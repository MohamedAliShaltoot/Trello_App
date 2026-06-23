package com.example.trello.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trello.data.local.entity.CardListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardListDao {

    @Query("SELECT * FROM lists WHERE boardId = :boardId ORDER BY position ASC")
    fun getListsForBoard(boardId: Long): Flow<List<CardListEntity>>

    @Insert
    suspend fun insertList(list: CardListEntity): Long

    @Update
    suspend fun updateList(list: CardListEntity)

    @Delete
    suspend fun deleteList(list: CardListEntity)

    @Query("SELECT MAX(position) FROM lists WHERE boardId = :boardId")
    suspend fun getMaxPosition(boardId: Long): Double?
}