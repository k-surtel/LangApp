package com.ks.langapp.ui.listOfDecks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ks.langapp.database.LangDatabaseDao

class ListOfDecksViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _navigateToDeck = MutableLiveData<Long>()
    val navigateToCards: LiveData<Long> get() = _navigateToDeck
    private val _navigateToReview = MutableLiveData<Long>()
    val navigateToEditDeck: LiveData<Long> get() = _navigateToReview

    val decks = database.getAllDecks()

    fun onDeckClick(deckId: Long) { _navigateToDeck.value = deckId }
    fun onDeckNavigated() { _navigateToDeck.value = Long.MIN_VALUE }
    fun onReviewButtonClick(deckId: Long) { _navigateToReview.value = deckId }
    fun onReviewNavigated() { _navigateToReview.value = Long.MIN_VALUE }
}