package com.ks.langapp.ui.flashcard

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.CardStats
import com.ks.langapp.data.database.entities.DeckStats
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: LangRepository,
    private val textToSpeech: TextToSpeech
    ) : ViewModel() {

    private var cardsReviewed: Int = 0

    private val  _cards = MutableStateFlow<List<Card>>(listOf())
    val cards: StateFlow<List<Card>> = _cards

    private val _backVisibility = MutableStateFlow(View.GONE)
    val backVisibility: StateFlow<Int> = _backVisibility

    private var _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _currentCard = MutableStateFlow<Card?>(null)
    val currentCard: StateFlow<Card?> = _currentCard

    private var startTime = System.currentTimeMillis()

    private var cardStats = mutableListOf<CardStats>()

    //todo save deck only if at least one reviewed
    // todo remove stats showing

    val currentEaseScore = MutableStateFlow(0)
    val currentTimesReviewed = MutableStateFlow(0)
    var divided = MutableStateFlow(0F)

    private fun tempFun() {
        val cardStat = cardStats.find { it.cardId == currentCard.value?.cardId }
        cardStat?.let {
            currentEaseScore.value = it.easeScore
            currentTimesReviewed.value = it.timesReviewed
            divided.value = currentEaseScore.value.toFloat() / currentTimesReviewed.value
        }
    }

    fun processArguments(deckId: Long) {
        if(deckId != Long.MIN_VALUE) {
            viewModelScope.launch {
                repository.getCardsOfADeck(deckId).collectLatest {
                    _cards.value = it.shuffled()

                    _currentIndex.value = 0
                    setFlashcard()
                    }
                }

            viewModelScope.launch {
                cardStats = repository.getCardStatsOfADeck(deckId).toMutableList()
            }
        }
    }

    private fun setFlashcard() {
        val shouldDelay = backVisibility.value == View.VISIBLE
        if (shouldDelay) _backVisibility.value = View.GONE
        viewModelScope.launch {
            if (shouldDelay) delay(610)
            _currentCard.value = cards.value[_currentIndex.value]
            tempFun() /////////////////////////////////////////////////////////////todo
        }
    }

    fun onCardClicked() {
        if (_backVisibility.value == View.GONE) _backVisibility.value = View.VISIBLE
        else _backVisibility.value = View.GONE
    }

    fun onListenClick() {
        currentCard.value?.let { textToSpeech.speak(it.back) }
    }

    fun onWrongClick() {
        setCurrentCardStats(0)
        if(cards.value.size > _currentIndex.value + 1) _currentIndex.value++
        else {
            _currentIndex.value = 0
            _cards.value = _cards.value.shuffled()
        }
        setFlashcard()
    }

    fun onRightClick() {
        setCurrentCardStats(1)
        if(cards.value.size > _currentIndex.value + 1) _currentIndex.value++
        else {
            _currentIndex.value = 0
            _cards.value = _cards.value.shuffled()
        }
        setFlashcard()
    }

    fun onEasyClick() {
        setCurrentCardStats(5)
        if(cards.value.size > _currentIndex.value + 1) _currentIndex.value++
        else {
            _currentIndex.value = 0
            _cards.value = _cards.value.shuffled()
        }
        setFlashcard()
    }

    private fun setCurrentCardStats(ease: Int) {
        cardsReviewed++

        var cardStat = cardStats.find { it.cardId == currentCard.value!!.cardId }

        if (cardStat == null) {
            cardStats.add(CardStats(currentCard.value!!.cardId,
                currentCard.value!!.deckId, 1, ease))
        } else {
            cardStat.timesReviewed++
            cardStat.easeScore += ease
        }

        // todo delete later
        for (stat in cardStats) {
            Log.d("LANGUS", "ID: ${stat.cardId}, " +
                    "TIMES REVIEWED: ${stat.timesReviewed}, EASE: ${stat.easeScore}")
        }
    }

    fun saveStats() {
        val timeDifference = System.currentTimeMillis() - startTime

        viewModelScope.launch {
            repository.saveCardStatsList(cardStats)
        }

        viewModelScope.launch {
            repository.saveDeckStats(DeckStats(
                0,
                currentCard.value!!.deckId,
                Calendar.getInstance().time,
                timeDifference,
                cardsReviewed
            ))
        }
    }
}