package com.ks.langapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_decks")
data class Deck (

    @PrimaryKey(autoGenerate = true)
    val deckId: Long = 0L,

    @ColumnInfo
    var name: String
)
