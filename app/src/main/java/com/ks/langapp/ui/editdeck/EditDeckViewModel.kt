package com.ks.langapp.ui.editdeck

import android.app.Application
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ks.langapp.database.LangDatabaseDao
import com.ks.langapp.database.entities.Deck
import kotlinx.coroutines.launch

class EditDeckViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    private val _deck = MutableLiveData<Deck?>()
    val deck: LiveData<Deck?> get() = _deck

    fun processArguments(deckId: Long) {
        if(deckId != Long.MIN_VALUE) {
            viewModelScope.launch { _deck.value = database.getDeck(deckId) }
        } else {
            _deck.value = null
        }
    }

    fun onSaveDeck(name: String) {
        viewModelScope.launch { insert(Deck(0,name)) }
    }

    fun onUpdateDeck(name: String) {
        viewModelScope.launch {
            _deck.value!!.name = name
            update(_deck.value!!)
        }
    }

    fun onDeleteDeck() {
        if(_deck.value != null) {
            viewModelScope.launch {
                deleteCardsOfADeck(deck.value!!)
                delete(deck.value!!)
            }
            navigateBack()
        }
    }

    private suspend fun insert(deck: Deck) { database.insert(deck) }
    private suspend fun update(deck: Deck) { database.update(deck) }
    private suspend fun deleteCardsOfADeck(deck: Deck) { database.deleteCardsOfADeck(deck.deckId) }
    private suspend fun delete(deck: Deck) { database.deleteDeck(deck.deckId) }

    private fun navigateBack() { _navigateBack.value = true }
    fun onNavigateBackCalled() { _navigateBack.value = false }
}