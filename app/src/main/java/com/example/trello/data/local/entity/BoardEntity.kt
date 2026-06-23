package com.example.trello.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)