package com.example.trello.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trello.data.local.dao.BoardDao
import com.example.trello.data.local.dao.CardDao
import com.example.trello.data.local.dao.CardListDao
import com.example.trello.data.local.entity.BoardEntity
import com.example.trello.data.local.entity.CardEntity
import com.example.trello.data.local.entity.CardListEntity

@Database(
    entities = [BoardEntity::class, CardListEntity::class, CardEntity::class],
    version = 4,
    exportSchema = false
)
abstract class TrelloDatabase : RoomDatabase() {
    abstract fun boardDao(): BoardDao
    abstract fun cardListDao(): CardListDao
    abstract fun cardDao(): CardDao
}
