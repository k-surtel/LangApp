package com.ks.langapp.data.database.entities

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
    var front: String,

    @ColumnInfo
    var back: String
)
