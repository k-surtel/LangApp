package com.ks.langapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_cards")
data class Card (

    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0L,

    @ColumnInfo
    var deckId: Long,

    @ColumnInfo
    var word: String,

    @ColumnInfo
    var description: String
)
