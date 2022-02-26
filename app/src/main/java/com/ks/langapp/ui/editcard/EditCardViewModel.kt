package com.ks.langapp.ui.editcard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ks.langapp.database.LangDatabaseDao
import com.ks.langapp.database.entities.Card
import kotlinx.coroutines.launch

class EditCardViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    private val _card = MutableLiveData<Card?>()
    val card: LiveData<Card?> get() = _card

    fun processArguments(cardId: Long) {
        if(cardId != Long.MIN_VALUE) {
            viewModelScope.launch { _card.value = database.getCard(cardId) }
        } else _card.value = null
    }

    fun onSaveCard(deckId: Long, word: String, description: String) {
        viewModelScope.launch {
            insert(Card(0,deckId, word, description))
        }
    }

    fun onUpdateCard(word: String, description: String) {
        viewModelScope.launch {
            _card.value!!.word = word
            _card.value!!.description = description
            update(_card.value!!)
        }
    }

    fun onDeleteCard() {
        viewModelScope.launch {
            database.deleteCard(card.value!!.cardId)
        }
        navigateBack()
    }

    private suspend fun insert(card: Card) { database.insert(card) }
    private suspend fun update(card: Card) { database.update(card) }

    private fun navigateBack() { _navigateBack.value = true }
    fun onNavigateBackCalled() { _navigateBack.value = false }
}