package com.ks.langapp.ui.decks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ks.langapp.database.LangDatabaseDao

class DecksViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _navigateToCards = MutableLiveData<Long>()
    val navigateToCards: LiveData<Long> get() = _navigateToCards
    private val _navigateToEditDeck = MutableLiveData<Long>()
    val navigateToEditDeck: LiveData<Long> get() = _navigateToEditDeck
    private val _navigateToImport = MutableLiveData<Boolean>()
    val navigateToImport: LiveData<Boolean> get() = _navigateToImport

    val decks = database.getAllDecks()


    fun onDeckClick(deckId: Long) { _navigateToCards.value = deckId }
    fun onCardsListNavigated() { _navigateToCards.value = Long.MIN_VALUE }
    fun onEditButtonClick(deckId: Long) { _navigateToEditDeck.value = deckId }
    fun onEditDeckNavigated() { _navigateToEditDeck.value = Long.MIN_VALUE }
    fun onImportClick() { _navigateToImport.value = true}
    fun onImportNavigated() { _navigateToImport.value = false }
}