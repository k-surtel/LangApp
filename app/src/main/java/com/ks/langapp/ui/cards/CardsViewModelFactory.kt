package com.ks.langapp.ui.cards

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.database.LangDatabaseDao
import java.lang.IllegalArgumentException

class CardsViewModelFactory(private val dataSource: LangDatabaseDao, private val application: Application)
    : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            return CardsViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}