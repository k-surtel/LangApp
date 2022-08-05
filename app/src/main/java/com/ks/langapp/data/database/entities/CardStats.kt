package com.ks.langapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_card_stats")
data class CardStats(
    @PrimaryKey
    val cardId: Long,

    @ColumnInfo
    val deckId: Long,

    @ColumnInfo
    var timesReviewed: Int,

    @ColumnInfo
    var easeScore: Int
)