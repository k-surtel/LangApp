package com.ks.langapp.ui.flashcard

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.CardStats
import com.ks.langapp.data.database.entities.DeckStats
import com.ks.langapp.data.repository.LangRepository
import com.ks.langapp.data.utils.TextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: LangRepository,
    private val textToSpeech: TextToSpeech
) : ViewModel() {

    private val _event = MutableSharedFlow<FlashcardEvent>()
    val event: SharedFlow<FlashcardEvent> = _event

    private val _cards = MutableStateFlow<List<Card>>(listOf())
    val cards: StateFlow<List<Card>> = _cards

    private var cardsOrder = mutableListOf<Int>()

    private var _cardsReviewed = MutableStateFlow(0)
    var cardsReviewed: StateFlow<Int> = _cardsReviewed

    private val _currentCard = MutableStateFlow<Card?>(null)
    val currentCard: StateFlow<Card?> = _currentCard

    private val _backVisibility = MutableStateFlow(View.GONE)
    val backVisibility: StateFlow<Int> = _backVisibility

    private val _currentStats = MutableStateFlow<CardStats?>(null)
    val currentStats: StateFlow<CardStats?> = _currentStats

    private var startTime = System.currentTimeMillis()

    private var cardStats = mutableListOf<CardStats>()


    fun processArguments(deckId: Long) {
        if (deckId != Long.MIN_VALUE) {
            viewModelScope.launch {
                repository.getCardsOfADeck(deckId).collectLatest {
                    _cards.value = it
                    shuffleCardsOrder(it.size)
                }
            }

            viewModelScope.launch {
                cardStats = repository.getCardStatsOfADeck(deckId).toMutableList()
            }

            viewModelScope.launch {
                val deck = repository.getDeck(deckId)
                deck?.backLanguage?.let { textToSpeech.setLanguage(it) }
            }
        }
    }

    private fun shuffleCardsOrder(numberOfCards: Int) {
        if (cardsOrder.isEmpty()) {
            cardsOrder = (0 until numberOfCards).toMutableList()
            cardsOrder.shuffle()
        }
        setFlashcard()
    }


    private fun setFlashcard() {
        val shouldDelay = backVisibility.value == View.VISIBLE
        if (shouldDelay) _backVisibility.value = View.GONE
        viewModelScope.launch {
            if (shouldDelay) delay(610)
            _currentCard.value = cards.value[cardsOrder[cardsReviewed.value]]
            _currentStats.value = cardStats.find { it.cardId == _currentCard.value!!.cardId }
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
        onNextFlashcard()
    }

    fun onRightClick() {
        setCurrentCardStats(1)
        onNextFlashcard()
    }

    fun onEasyClick() {
        setCurrentCardStats(5)
        onNextFlashcard()
    }

    private fun setCurrentCardStats(ease: Int) {
        _cardsReviewed.value++

        val cardStat = cardStats.find { it.cardId == currentCard.value!!.cardId }

        if (cardStat == null) {
            cardStats.add(
                CardStats(
                    currentCard.value!!.cardId,
                    currentCard.value!!.deckId, 1, ease
                )
            )
        } else {
            cardStat.timesReviewed++
            cardStat.easeScore += ease
        }
    }

    private fun onNextFlashcard() {
        if (cardsReviewed.value < cards.value.size) setFlashcard()
        else {
            viewModelScope.launch { _event.emit(FlashcardEvent.NAVIGATE_BACK) }
        }
    }

    fun saveStats(): DeckStats? {
        if (cardsReviewed.value == 0) return null

        val timeDifference = System.currentTimeMillis() - startTime

        viewModelScope.launch {
            repository.saveCardStatsList(cardStats)
        }

        val deckStats = DeckStats(
            0,
            currentCard.value!!.deckId,
            Calendar.getInstance().time,
            timeDifference,
            cardsReviewed.value
        )

        viewModelScope.launch {
            repository.saveDeckStats(deckStats)
        }

        return deckStats
    }

    fun saveCard(front: String, back: String) = viewModelScope.launch {
        currentCard.value?.let {
            if (it.front != front || it.back != back)
                repository.saveCard(
                    it.copy(
                        front = front,
                        back = back
                    )
                )
        }
    }

    enum class FlashcardEvent {
        NAVIGATE_BACK
    }
}