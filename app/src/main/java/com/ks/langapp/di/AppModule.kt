package com.ks.langapp.di

import android.content.Context
import androidx.room.Room
import com.ks.langapp.data.database.LangDatabase
import com.ks.langapp.data.repository.LangRepository
import com.ks.langapp.data.repository.LangRepositoryImpl
import com.ks.langapp.ui.utils.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLangDatabase(
        @ApplicationContext context: Context
    ): LangDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LangDatabase::class.java,
            LangDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideLangRepository(
        database: LangDatabase
    ): LangRepository = LangRepositoryImpl(database.langDatabaseDao)

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider = ResourceProvider(context)
}