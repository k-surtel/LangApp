package com.ks.langapp.ui.deck

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    var deckId: Long? = null
    var deck: Deck? = null
    lateinit var cards: Flow<List<Card>>


    fun processArguments(deckId: Long) {
        this.deckId = deckId
        cards = repository.getCardsOfADeck(deckId)

        viewModelScope.launch {

            deck = repository.getDeck(deckId)
            deck?.let { deck ->
                cards.collectLatest {
                    if (it.size != deck.cardsCount)
                        repository.saveDeck(Deck(deck.deckId, deck.name, it.size))
                }
            }


        }
    }
}