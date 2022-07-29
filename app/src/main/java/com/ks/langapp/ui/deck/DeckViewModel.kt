package com.ks.langapp.ui.deck

import android.util.Log
import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    var deckId: Long? = null

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck

    lateinit var cards: Flow<List<Card>>

    fun processArguments(deckId: Long) {
        this.deckId = deckId
        cards = repository.getCardsOfADeck(deckId)

        viewModelScope.launch {
            _deck.value = repository.getDeck(deckId)
        }
    }

    fun updateCardsCount(count: Int) {
        Log.d("LANGUS", "UPDATE CARDS COUNT")

        _deck.value?.let {
            it.cardsCount = count
            viewModelScope.launch {
                repository.saveDeck(it)
            }
        }
    }
}