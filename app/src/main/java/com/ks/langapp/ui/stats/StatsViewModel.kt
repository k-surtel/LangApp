package com.ks.langapp.ui.stats

import androidx.lifecycle.*
import com.ks.langapp.data.database.entities.DeckStats
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

    private val _deckStats = MutableStateFlow<List<DeckStats>>(emptyList())
    val deckStats = _deckStats

    fun loadDeckStats() {
        viewModelScope.launch {
            _deckStats.value = repository.getDeckStats()
        }
    }
}