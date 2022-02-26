package com.ks.langapp.ui.importcards

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.database.LangDatabaseDao
import java.lang.IllegalArgumentException

class ImportViewModelFactory(private val dataSource: LangDatabaseDao, private val application: Application)
    : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImportViewModel::class.java)) {
            return ImportViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}