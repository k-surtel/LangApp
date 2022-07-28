package com.ks.langapp.ui.listOfDecks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListOfDecksViewModel @Inject constructor(
    private val repository: LangRepository
    ) : ViewModel() {

    val decks = repository.getAllDecks()

    fun getCardsCounts() = viewModelScope.launch {
        val counts = repository.getCardsCounts()
    }
}