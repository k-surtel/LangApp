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

    private val _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack: SharedFlow<Boolean> = _navigateBack

    private val _fileName = MutableStateFlow(resourceProvider.getString(R.string.no_file_selected))
    val fileName: StateFlow<String> = _fileName

    private val _firstTerm = MutableStateFlow(Term.FRONT)
    val firstTerm: StateFlow<Term> = _firstTerm

    private val _termsSeparator = MutableStateFlow<Separator>(Separator.CharSeparator(';'))
    val termsSeparator: StateFlow<Separator> = _termsSeparator

    private val _cardsSeparator = MutableStateFlow<Separator>(Separator.NewLine)
    val cardsSeparator: StateFlow<Separator> = _cardsSeparator

    private val cards = mutableListOf<Card>()


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
            if (cardsSeparator.value is Separator.NewLine) textLine?.let { assembleCard(it) }
            else stringBuilder.append(textLine)

        }
        val fileContent = stringBuilder.toString()
        if (cardsSeparator.value !is Separator.NewLine) divideContentToCards(fileContent)
    }

    @SuppressLint("Range")
    private fun setFileName(uri: Uri) {
        val cursor = resourceProvider.getContentResolver().query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ?: ""
        cursor?.close()

        _fileName.value = fileName
    }

    private fun divideContentToCards(fileContent: String) {
        val cardsContents = fileContent.split(';') //todo
        for (substring in cardsContents) {
            assembleCard(substring)
        }
        //else  todo error message separator not set

    }

    private fun assembleCard(cardContent: String) {
        val definitions = cardContent.split(';') //todo
        cards.add(Card(0, Long.MIN_VALUE, definitions[0], definitions[1]))
    }


    fun onImportExampleClicked() {
        viewModelScope.launch {
            val deckId = insertDeck(Deck(0, "Example Deck", 26))
            prepareExampleList(deckId)
            _navigateBack.emit(true)
        }
    }

    fun onImportClick(name: String) {
        if (cards.isNotEmpty()) {
            viewModelScope.launch {
                val deckId = insertDeck(Deck(0, name, cards.size))
                saveCardsOfDeck(deckId)
                _navigateBack.emit(true)
            }
        } //else // todo error message
    }

    private fun saveCardsOfDeck(deckId: Long) {
        for (card in cards) {
            card.deckId = deckId
        }

        viewModelScope.launch {
            insertCards(cards)
        }
    }

    fun prepareExampleList(deckId: Long) {
        val exampleList = listOf(
            Card(0, deckId, "знать", "to know, be aware"),
            Card(0, deckId, "мой", "my, mine"),
            Card(0, deckId, "до", "to, up to, about, before"),
            Card(0, deckId, "или", "or"),
            Card(0, deckId, "если", "if"),
            Card(0, deckId, "время", "time, season"),
            Card(0, deckId, "рука", "hand, arm"),
            Card(0, deckId, "нет", "no, not, but"),
            Card(0, deckId, "самый", "most, the very, the same"),
            Card(0, deckId, "большой", "big, large, important"),
            Card(0, deckId, "даже", "even"),
            Card(0, deckId, "наш", "our, ours"),
            Card(0, deckId, "где", "where"),
            Card(0, deckId, "дело", "business, affair, matter"),
            Card(0, deckId, "там", "there, then"),
            Card(0, deckId, "глаз", "eye, sight"),
            Card(0, deckId, "жизнь", "life"),
            Card(0, deckId, "день", "day"),
            Card(0, deckId, "ничто", "nothing"),
            Card(0, deckId, "потом", "afterwards, then"),
            Card(0, deckId, "очень", "very"),
            Card(0, deckId, "голова", "head, mind, brains"),
            Card(0, deckId, "без", "without"),
            Card(0, deckId, "видеть", "to see"),
            Card(0, deckId, "друг", "friend"),
            Card(0, deckId, "дом", "house, home")
        )

        viewModelScope.launch {
            insertCards(exampleList)
        }
    }

    private suspend fun insertDeck(deck: Deck): Long {
        return repository.saveDeck(deck)
    }

    private suspend fun insertCards(cards: List<Card>) {
        repository.saveAllCards(cards)
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
}