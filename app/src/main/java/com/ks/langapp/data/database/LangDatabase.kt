package com.ks.langapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck

@Database(entities = [Card::class, Deck::class], version = 12, exportSchema = false)
abstract class LangDatabase : RoomDatabase() {

    abstract val langDatabaseDao: LangDatabaseDao

    companion object {
        const val DATABASE_NAME = "lang_database"
    }
}