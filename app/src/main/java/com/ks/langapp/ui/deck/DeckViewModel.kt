package com.ks.langapp.ui.deck

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.CardStats
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck

    lateinit var cards: Flow<List<Card>>
    lateinit var cardStats: List<CardStats>

    private var editedCard: Card? = null

    private val _showUndoForCardDeletion = MutableSharedFlow<Boolean>()
    val showUndoForCardDeletion = _showUndoForCardDeletion.asSharedFlow()

    fun processArguments(deckId: Long) {
        cards = repository.getCardsOfADeck(deckId)

        viewModelScope.launch {
            _deck.value = repository.getDeck(deckId)
        }

        viewModelScope.launch {
            cardStats = repository.getCardStatsOfADeck(deckId)
        }
    }

    fun checkForUpdates(cardsList: List<Card>) {
        _deck.value?.let { updateDeckCardsCount(it, cardsList.size) }
        checkIfCardDeleted(cardsList)
    }

    private fun updateDeckCardsCount(deck: Deck, cardsCount: Int) {
        if (deck.cardsCount != cardsCount) {
            deck.cardsCount = cardsCount
            viewModelScope.launch {
                repository.saveDeck(deck)
            }
        }
    }

    fun selectEditedCard(card: Card) { editedCard = card }

    private fun checkIfCardDeleted(cardsList: List<Card>) {
        if (editedCard != null && !cardsList.contains(editedCard)) {
            viewModelScope.launch {
                _showUndoForCardDeletion.emit(true)
                editedCard = null
            }
        }
    }

    fun undoCardDeletion() = viewModelScope.launch {
        repository.undoCardDeletion()
        _deck.value?.let {
            _deck.value = it.copy(cardsCount = it.cardsCount + 1)
            repository.saveDeck(_deck.value!!)
        }
    }
}