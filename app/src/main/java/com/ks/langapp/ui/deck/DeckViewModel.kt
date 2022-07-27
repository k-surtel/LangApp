package com.ks.langapp.ui.deck

import android.app.Application
import androidx.lifecycle.*
import com.ks.langapp.data.database.LangDatabaseDao
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    private val _navigateToEditCard = MutableLiveData<Card?>() //todo
    val navigateToEditCard: LiveData<Card?> get() = _navigateToEditCard
    private val _navigateToFlashcards = MutableLiveData<Boolean>()
    val navigateToFlashcards: LiveData<Boolean> get() = _navigateToFlashcards

    var deckId: Long? = null
    //var deck: Deck? = null  //todo
    lateinit var cards: Flow<List<Card>>


    fun processArguments(deckId: Long) {
        this.deckId = deckId
        cards = repository.getCardsOfADeck(deckId)

        viewModelScope.launch {
            //deck = database.getDeck(deckId)
        }
    }

    fun onCardClick(card: Card) { _navigateToEditCard.value = card }
    fun onEditCardNavigated() { _navigateToEditCard.value = null }

    fun onReviseButtonClick() { _navigateToFlashcards.value = true }
    fun onFlashcardsNavigated() { _navigateToFlashcards.value = false }
}