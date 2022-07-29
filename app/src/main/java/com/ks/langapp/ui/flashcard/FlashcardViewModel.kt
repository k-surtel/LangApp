package com.ks.langapp.ui.flashcard

import android.view.View
import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: LangRepository
    ) : ViewModel() {

    private val  _cards = MutableStateFlow<List<Card>>(listOf())
    val cards: StateFlow<List<Card>> = _cards

    private val _backVisibility = MutableStateFlow(View.GONE)
    val backVisibility: StateFlow<Int> = _backVisibility

    private var _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _currentCard = MutableStateFlow<Card?>(null)
    val currentCard: StateFlow<Card?> = _currentCard

    fun processArguments(deckId: Long) {
        if(deckId != Long.MIN_VALUE) {
            viewModelScope.launch {
                repository.getCardsOfADeck(deckId).collectLatest {
                    _cards.value = it.shuffled()

                    _currentIndex.value = 0
                    setFlashcard()
                }
            }
        }
    }

    private fun setFlashcard() {
        val shouldDelay = backVisibility.value == View.VISIBLE

        if (shouldDelay) _backVisibility.value = View.GONE
        viewModelScope.launch {
            if (shouldDelay) delay(610)
            _currentCard.value = cards.value[_currentIndex.value]
        }
    }

    fun onCardClicked() {
        if (_backVisibility.value == View.GONE) _backVisibility.value = View.VISIBLE
        else _backVisibility.value = View.GONE
    }

    fun onWrongClicked() {
        if(cards.value.size > _currentIndex.value + 1) _currentIndex.value++
        else {
            _currentIndex.value = 0
            _cards.value = _cards.value.shuffled()
        }
        setFlashcard()
    }
}