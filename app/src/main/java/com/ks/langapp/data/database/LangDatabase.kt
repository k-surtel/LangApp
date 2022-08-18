package com.ks.langapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.CardStats
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.database.entities.DeckStats
import com.ks.langapp.data.utils.Converters

@Database(
    entities = [Card::class, CardStats::class, Deck::class, DeckStats::class],
    version = 16,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class LangDatabase : RoomDatabase() {

    abstract val langDatabaseDao: LangDatabaseDao

    companion object {
        const val DATABASE_NAME = "lang_database"
    }
}