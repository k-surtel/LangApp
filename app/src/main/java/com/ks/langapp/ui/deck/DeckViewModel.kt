package com.ks.langapp.ui.deck

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ks.langapp.database.LangDatabaseDao
import com.ks.langapp.database.entities.Card
import com.ks.langapp.database.entities.Deck
import kotlinx.coroutines.launch

class DeckViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _navigateToEditCard = MutableLiveData<Card?>()
    val navigateToEditCard: LiveData<Card?> get() = _navigateToEditCard
    private val _navigateToFlashcards = MutableLiveData<Boolean>()
    val navigateToFlashcards: LiveData<Boolean> get() = _navigateToFlashcards

    var deckId: Long? = null
    var deck: Deck? = null
    lateinit var cards: LiveData<List<Card>>


    fun processArguments(deckId: Long) {
        this.deckId = deckId
        cards = database.getCardsOfDeck(deckId)

        viewModelScope.launch {
            deck = database.getDeck(deckId)
        }
    }

    fun onCardClick(card: Card) { _navigateToEditCard.value = card }
    fun onEditCardNavigated() { _navigateToEditCard.value = null }

    fun onReviseButtonClick() { _navigateToFlashcards.value = true }
    fun onFlashcardsNavigated() { _navigateToFlashcards.value = false }
}