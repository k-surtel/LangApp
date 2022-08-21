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

    private val _event = MutableSharedFlow<EditDeckEvent>()
    val event = _event.asSharedFlow()

    private var navigatedFromDeckFragment = false

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck

    private var frontLanguage: String? = null
    private var backLanguage: String? = null

    fun processArguments(deckId: Long, navigatedFromDeckFragment: Boolean) {
        this.navigatedFromDeckFragment = navigatedFromDeckFragment

        viewModelScope.launch {
            if (deckId != Long.MIN_VALUE) {
                _deck.value = repository.getDeck(deckId)
            }
            _event.emit(EditDeckEvent.DECK_LOADED)
        }
    }

    fun setLanguage(language: String?, side: EditDeckFragment.Side) {
        when (side) {
            EditDeckFragment.Side.FRONT -> frontLanguage = language
            EditDeckFragment.Side.BACK -> backLanguage = language
        }
    }

    fun onSaveDeck(name: String) = viewModelScope.launch {
        repository.saveDeck(
            Deck(
                deck.value?.deckId ?: 0,
                name,
                deck.value?.cardsCount ?: 0,
                frontLanguage,
                backLanguage)
        )
        _event.emit(EditDeckEvent.NAVIGATE_BACK)
    }

    fun onDeleteDeck() {
        _deck.value?.let {
            viewModelScope.launch {
                repository.deleteDeck(it)
                if (navigatedFromDeckFragment) _event.emit(EditDeckEvent.NAVIGATE_BACK_SKIP_DECK_FRAGMENT)
                else _event.emit(EditDeckEvent.NAVIGATE_BACK)
            }
        }
    }

    enum class EditDeckEvent {
        NAVIGATE_BACK, NAVIGATE_BACK_SKIP_DECK_FRAGMENT, DECK_LOADED
    }
}