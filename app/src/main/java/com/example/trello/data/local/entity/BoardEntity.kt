package com.example.trello.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val colorHex: String = "#0079BF",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
