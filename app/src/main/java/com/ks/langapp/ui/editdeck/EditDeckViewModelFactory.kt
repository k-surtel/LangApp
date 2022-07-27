package com.ks.langapp.ui.editdeck

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.data.database.LangDatabaseDao
import java.lang.IllegalArgumentException

class EditDeckViewModelFactory(private val dataSource: LangDatabaseDao, private val application: Application)
    : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EditDeckViewModel::class.java)) {
            return EditDeckViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}