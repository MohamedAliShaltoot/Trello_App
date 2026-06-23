package com.example.trello.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trello.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards WHERE listId = :listId ORDER BY position ASC")
    fun getCardsForList(listId: Long): Flow<List<CardEntity>>

    @Insert
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT MAX(position) FROM cards WHERE listId = :listId")
    suspend fun getMaxPosition(listId: Long): Double?
}