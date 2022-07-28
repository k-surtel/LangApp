package com.ks.langapp.ui.editdeck

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDeckViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck

    fun processArguments(deckId: Long) {
        if(deckId != Long.MIN_VALUE) {
            viewModelScope.launch { _deck.value = repository.getDeck(deckId) }
        } else {
            _deck.value = null
        }
    }

    fun onSaveDeck(name: String) = viewModelScope.launch {
        val deckId = repository.saveDeck(Deck(deck.value?.deckId ?: 0, name))
    }

    fun onDeleteDeck() {
        _deck.value?.let {
            viewModelScope.launch {
                repository.deleteDeck(it)
                navigateBack()
            }
        }
    }

    private fun navigateBack() { _navigateBack.value = true }
    fun onNavigateBackCalled() { _navigateBack.value = false }
}