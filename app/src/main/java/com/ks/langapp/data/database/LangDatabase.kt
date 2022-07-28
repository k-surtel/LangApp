package com.ks.langapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck

@Database(entities = [Card::class, Deck::class], version = 11, exportSchema = false)
abstract class LangDatabase : RoomDatabase() {

    abstract val langDatabaseDao: LangDatabaseDao

    companion object {
        const val DATABASE_NAME = "lang_database"

        @Volatile
        private var INSTANCE: LangDatabase? = null

        fun getInstance(context: Context): LangDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        LangDatabase::class.java, "lang_database")
                        .fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}