package com.ks.langapp.ui.listOfDecks

import androidx.lifecycle.ViewModel
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListOfDecksViewModel @Inject constructor(
    repository: LangRepository
    ) : ViewModel() {

    val decks = repository.getAllDecks()
}