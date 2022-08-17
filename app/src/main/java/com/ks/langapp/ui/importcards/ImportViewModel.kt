package com.ks.langapp.ui.importcards

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.langapp.R
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.repository.LangRepository
import com.ks.langapp.ui.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val repository: LangRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _event = MutableSharedFlow<ImportEvent>()
    val event: SharedFlow<ImportEvent> = _event

    private val _fileName = MutableStateFlow(resourceProvider.getString(R.string.no_file_selected))
    val fileName: StateFlow<String> = _fileName

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck

    private val _firstTerm = MutableStateFlow(Term.FRONT)
    val firstTerm: StateFlow<Term> = _firstTerm

    private val _termsSeparator = MutableStateFlow<Separator>(Separator.CharSeparator(';'))
    val termsSeparator: StateFlow<Separator> = _termsSeparator

    private val _cardsSeparator = MutableStateFlow<Separator>(Separator.NewLine)
    val cardsSeparator: StateFlow<Separator> = _cardsSeparator

    private val cards = mutableListOf<Card>()

    private var fileContent = ""

    fun getAllDecks() = repository.getAllDecks()

    fun chooseDeck(deck: Deck) {
        _deck.value = deck
    }

    fun onFirstTermButtonClick() {
        _firstTerm.value = when (firstTerm.value) {
            Term.FRONT -> Term.BACK
            Term.BACK -> Term.FRONT
        }
    }

    fun onSeparatorChange(separatorType: SeparatorType, separator: Separator) {
        when (separatorType) {
            SeparatorType.TERMS -> _termsSeparator.value = separator
            SeparatorType.CARDS -> _cardsSeparator.value = separator
        }
    }

    fun processUri(uri: Uri, contentResolver: ContentResolver) {
        setFileName(uri)

        val inputStreamReader = InputStreamReader(contentResolver.openInputStream(uri))
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder = StringBuilder()
        var textLine: String?
        while (bufferedReader.readLine().also { textLine = it } != null) {
                stringBuilder.append(textLine)
                stringBuilder.append("\r\n")
        }
        val fileContent = stringBuilder.toString()
        this.fileContent = fileContent
    }

    @SuppressLint("Range")
    private fun setFileName(uri: Uri) {
        val cursor = resourceProvider.getContentResolver().query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ?: ""
        cursor?.close()

        _fileName.value = fileName
    }

    fun onImportClick() {
        if (validateData()) {
            processFile()
            if (cards.isNotEmpty()) {
                updateCardsCount()
                saveCards()
                viewModelScope.launch { _event.emit(ImportEvent.NAVIGATE_BACK) }
            } else viewModelScope.launch { _event.emit(ImportEvent.NO_CARDS_CREATED) }
        }
    }

    private fun validateData(): Boolean {
        return if (fileName.equals(resourceProvider.getString(R.string.no_file_selected))) {
            viewModelScope.launch { _event.emit(ImportEvent.NO_FILE_SELECTED) }
            false
        } else if (fileContent.isBlank()) {
            viewModelScope.launch { _event.emit(ImportEvent.BAD_FILE) }
            false
        } else if (deck.value == null) {
            viewModelScope.launch { _event.emit(ImportEvent.NO_DECK_SELECTED) }
            false
        } else true
    }

    private fun processFile() {
        val cardContents = when (cardsSeparator.value) {
            is Separator.NewLine -> fileContent.split("\r?\n|\r".toRegex()).toList()
            is Separator.CharSeparator ->
                fileContent.split((cardsSeparator.value as Separator.CharSeparator).char).toList()
        }
        for (cardContent in cardContents) createACard(cardContent)
    }

    private fun createACard(cardContent: String) {
        val terms = when (termsSeparator.value) {
            is Separator.NewLine -> cardContent.split("\r?\n|\r".toRegex()).toList()
            is Separator.CharSeparator ->
                cardContent.split((termsSeparator.value as Separator.CharSeparator).char).toList()
        }

        if (terms.size == 2 && deck.value != null) {
            val card = if (firstTerm.value == Term.FRONT) Card(0, deck.value!!.deckId, terms[0], terms[1])
            else Card(0, deck.value!!.deckId, terms[1], terms[0])

            cards.add(card)
        }
    }

    private fun saveCards() = viewModelScope.launch {
        repository.saveAllCards(cards)
    }

    private fun updateCardsCount() = viewModelScope.launch {
        deck.value?.let {
            repository.saveDeck(it.copy(
                cardsCount = it.cardsCount + cards.size
            ))
        }
    }

    fun saveDeck(deckName: String) = viewModelScope.launch {
        repository.saveDeck(Deck(name = deckName))
    }

    enum class Term {
        FRONT, BACK
    }

    sealed class Separator(char: Char?) {
        class CharSeparator(val char: Char) : Separator(char)
        object NewLine : Separator(null)
    }

    enum class SeparatorType {
        TERMS, CARDS
    }

    enum class ImportEvent {
        NAVIGATE_BACK, NO_FILE_SELECTED, BAD_FILE, NO_DECK_SELECTED, NO_CARDS_CREATED
    }
}