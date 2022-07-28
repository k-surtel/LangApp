package com.ks.langapp.ui.flashcard

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: LangRepository
    ) : ViewModel() {

    lateinit var cards: List<Card>

    private val _currentIndex = MutableLiveData<Int>()
    private val currentIndex: LiveData<Int> get() = _currentIndex

    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> get() = _currentWord


    fun processArguments(deckId: Long) {
        if(deckId != Long.MIN_VALUE) {
            viewModelScope.launch {
                repository.getCardsOfADeck(deckId).collectLatest {
                    cards = it.shuffled()

                    _currentIndex.value = 0
                    setFlashcard()
                }
            }
        }
    }

    fun setFlashcard() { _currentWord.value = cards[currentIndex.value!!].front }

    fun onCardClicked() {
        if(currentWord.value == cards[currentIndex.value!!].front)
            _currentWord.value = cards[currentIndex.value!!].back
        else
            _currentWord.value = cards[currentIndex.value!!].front
    }

    fun onWrongClicked() {
        if(cards.size > currentIndex.value!!.plus(1))
            _currentIndex.value = _currentIndex.value!!.plus(1)
        else _currentIndex.value = 0
        setFlashcard()
    }
}