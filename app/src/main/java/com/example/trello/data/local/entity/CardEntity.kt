package com.example.trello.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = CardListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val listId: Long,
    val title: String,
    val description: String? = null,
    val position: Double,
    val createdAt: Long = System.currentTimeMillis()
)