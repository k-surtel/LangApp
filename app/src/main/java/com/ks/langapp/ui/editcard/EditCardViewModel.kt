package com.ks.langapp.ui.editcard

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCardViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    private val _navigateBack = MutableLiveData<Boolean>() //todo
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    private val _card = MutableStateFlow<Card?>(null)
    val card: StateFlow<Card?> = _card

    fun processArguments(cardId: Long) {
        if (cardId != Long.MIN_VALUE) {
            viewModelScope.launch { _card.value = repository.getCard(cardId) }
        }
    }

    fun onSaveCard(deckId: Long, word: String, description: String) =
        viewModelScope.launch {
            repository.saveCard(Card(card.value?.cardId ?: 0, deckId, word, description))
        }

    fun onDeleteCard() = viewModelScope.launch {
        card.value?.let {
            repository.deleteCard(it)
        }
        navigateBack()
    }

    private fun navigateBack() {
        _navigateBack.value = true
    }

    fun onNavigateBackCalled() {
        _navigateBack.value = false
    }
}