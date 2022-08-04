package com.ks.langapp.ui.editdeck

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDeckViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    private val _navigateBackSkipDeckFragment = MutableSharedFlow<Boolean>()
    val navigateBackSkipDeckFragment = _navigateBackSkipDeckFragment.asSharedFlow()
    private var navigatedFromDeckFragment = false

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck

    fun processArguments(deckId: Long, navigatedFromDeckFragment: Boolean) {
        this.navigatedFromDeckFragment = navigatedFromDeckFragment

        if (deckId != Long.MIN_VALUE) {
            viewModelScope.launch { _deck.value = repository.getDeck(deckId) }
        } else {
            _deck.value = null
        }
    }

    fun onSaveDeck(name: String) = viewModelScope.launch {
        repository.saveDeck(
            Deck(deck.value?.deckId ?: 0, name, deck.value?.cardsCount ?: 0)
        )
        _navigateBackSkipDeckFragment.emit(false)
    }

    fun onDeleteDeck() {
        _deck.value?.let {
            viewModelScope.launch {
                repository.deleteDeck(it)
                _navigateBackSkipDeckFragment.emit(navigatedFromDeckFragment)
            }
        }
    }
}