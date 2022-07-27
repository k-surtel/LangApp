package com.ks.langapp.ui.importcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ks.langapp.data.database.LangDatabaseDao
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import kotlinx.coroutines.launch

class ImportViewModel(val database: LangDatabaseDao, application: Application) : AndroidViewModel(application) {

    fun onImportClicked() {
        onImportExampleDeck()
    }

    fun onImportExampleDeck() {
        viewModelScope.launch {
            val deckId = insertDeck(Deck(0, "Example Deck"))
            prepareExampleList(deckId)
        }
    }

    fun prepareExampleList(deckId: Long) {
        val exampleList = listOf(
            Card(0, deckId, "знать",    "to know, be aware"),
            Card(0, deckId, "мой",      "my, mine"),
            Card(0, deckId, "до",       "to, up to, about, before"),
            Card(0, deckId, "или",      "or"),
            Card(0, deckId, "если",     "if"),
            Card(0, deckId, "время",    "time, season"),
            Card(0, deckId, "рука",     "hand, arm"),
            Card(0, deckId, "нет",      "no, not, but"),
            Card(0, deckId, "самый",    "most, the very, the same"),
            Card(0, deckId, "большой",  "big, large, important"),
            Card(0, deckId, "даже",     "even"),
            Card(0, deckId, "наш",      "our, ours"),
            Card(0, deckId, "где",      "where"),
            Card(0, deckId, "дело",     "business, affair, matter"),
            Card(0, deckId, "там",      "there, then"),
            Card(0, deckId, "глаз",     "eye, sight"),
            Card(0, deckId, "жизнь",    "life"),
            Card(0, deckId, "день",     "day"),
            Card(0, deckId, "ничто",    "nothing"),
            Card(0, deckId, "потом",    "afterwards, then"),
            Card(0, deckId, "очень",    "very"),
            Card(0, deckId, "голова",   "head, mind, brains"),
            Card(0, deckId, "без",      "without"),
            Card(0, deckId, "видеть",   "to see"),
            Card(0, deckId, "друг",     "friend"),
            Card(0, deckId, "дом",      "house, home")
        )

        viewModelScope.launch {
            insertCards(exampleList)
        }
    }

    fun importTextFile() {
//        val intent = Intent()
//            .setType("*/*")
//            .setAction(Intent.ACTION_GET_CONTENT)
//
//        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
    }

    private suspend fun insertDeck(deck: Deck): Long { return database.insert(deck) }
    private suspend fun insertCards(cards: List<Card>) { database.insertAll(cards) }
}