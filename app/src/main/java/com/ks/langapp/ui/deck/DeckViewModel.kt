package com.ks.langapp.ui.deck

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    var deckId: Long? = null
    //var deck: Deck? = null  //todo
    lateinit var cards: Flow<List<Card>>


    fun processArguments(deckId: Long) {
        this.deckId = deckId
        cards = repository.getCardsOfADeck(deckId)

//        viewModelScope.launch {
//            deck = database.getDeck(deckId)
//        }
    }
}