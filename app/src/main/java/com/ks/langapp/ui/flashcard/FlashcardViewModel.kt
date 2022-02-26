package com.ks.langapp.ui.flashcard

import android.app.Application
import androidx.lifecycle.*
import com.ks.langapp.database.LangDatabaseDao
import com.ks.langapp.database.entities.Card

class FlashcardViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    lateinit var cards: LiveData<List<Card>>

    private val _currentIndex = MutableLiveData<Int>()
    private val currentIndex: LiveData<Int> get() = _currentIndex

    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> get() = _currentWord


    fun processArguments(deckId: Long) {
        if(deckId != Long.MIN_VALUE) {
            cards = Transformations.map(database.getCardsOfDeck(deckId)) { it.shuffled() }
            _currentIndex.value = 0
        }
    }

    fun setFlashcard() { _currentWord.value = cards.value!![currentIndex.value!!].word }

    fun onCardClicked() {
        if(currentWord.value == cards.value!![currentIndex.value!!].word)
            _currentWord.value = cards.value!![currentIndex.value!!].description
        else
            _currentWord.value = cards.value!![currentIndex.value!!].word
    }

    fun onWrongClicked() {
        if(cards.value!!.size > currentIndex.value!!.plus(1))
            _currentIndex.value = _currentIndex.value!!.plus(1)
        else _currentIndex.value = 0
        setFlashcard()
    }
}