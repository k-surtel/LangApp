package com.ks.langapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_deck_stats")
data class DeckStats(

    @PrimaryKey(autoGenerate = true)
    val deckStatId: Long,

    @ColumnInfo
    val deckId: Long,

    @ColumnInfo
    var date: Date,

    @ColumnInfo
    var time: Long
)
