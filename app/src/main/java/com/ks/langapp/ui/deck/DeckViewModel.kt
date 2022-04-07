package com.ks.langapp.ui.deck

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ks.langapp.database.LangDatabaseDao
import com.ks.langapp.database.entities.Card

class DeckViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _navigateToEditCard = MutableLiveData<Card?>()
    val navigateToEditCard: LiveData<Card?> get() = _navigateToEditCard
    private val _navigateToFlashcards = MutableLiveData<Boolean>()
    val navigateToFlashcards: LiveData<Boolean> get() = _navigateToFlashcards

    var deckId: Long? = null
    lateinit var cards: LiveData<List<Card>>


    fun processArguments(deckId: Long) {
        this.deckId = deckId
        cards = database.getCardsOfDeck(deckId)
    }

    fun onCardClick(card: Card) { _navigateToEditCard.value = card }
    fun onEditCardNavigated() { _navigateToEditCard.value = null }

    fun onReviseButtonClick() { _navigateToFlashcards.value = true }
    fun onFlashcardsNavigated() { _navigateToFlashcards.value = false }
}