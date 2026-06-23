package com.example.trello.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lists",
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["id"],
            childColumns = ["boardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("boardId")]
)
data class CardListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val boardId: Long,
    val title: String,
    // Double, not Int: moving a card between position 1 and 2 just becomes
    // 1.5, so a single drag never has to renumber every sibling row.
    val position: Double
)